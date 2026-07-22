package scraper

import (
	"context"
	"fmt"
	"net/http"
	"sync"
	"time"

	models "mathgate/internal/domain/exam"

	"github.com/PuerkitoBio/goquery"
)


var urlMap = map[string]string{
		"ege": "https://math-ege",
		"oge": "https://math-oge",
		"vpr8": "https://math8-vpr",
		"vpr7": "https://math7-vpr",
		"vpr6": "https://math6-vpr",
		"vpr5": "https://math5-vpr",
}

type HttpExamSource struct {
	client  *http.Client
	baseURL string
	dir string
}

func New(baseUrl string, dir string) HttpExamSource {
	return HttpExamSource{
		client: &http.Client{Timeout: 8 * time.Second},
		baseURL: baseUrl,
		dir: dir,
	}
}

func (s *HttpExamSource) FetchQuestion(ctx context.Context, id int, examType string) (*models.ExamQuestion, error) {
	

	url := fmt.Sprintf("%s.%s/problem?id=%d&print=true", urlMap[examType], s.baseURL, id,)

	req, err := http.NewRequestWithContext(ctx, http.MethodGet, url, nil)
	if err != nil {
		return nil, err
	}

	resp, err := s.client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode == http.StatusNotFound {
		return nil, fmt.Errorf("question %d not found", id)
	}
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("source returned status %d for question %d", resp.StatusCode, id)
	}

	doc, err := goquery.NewDocumentFromReader(resp.Body)
	if err != nil {
		return nil, err
	}

	parsed, err := parseQuestion(doc, url)
	if err != nil {
		return nil, err
	}

	var (
		taskBlocks []models.ExamBlock
		solutionBlocks []models.ExamBlock
	)

	var wg sync.WaitGroup
	wg.Add(2)

	go func() {
		defer wg.Done()
		taskBlocks = downloadImagesForBlocks(ctx, s.client, s.dir, parsed.taskBlocks)
	}()

	go func() {
		defer wg.Done()
		solutionBlocks = downloadImagesForBlocks(ctx, s.client, s.dir, parsed.solutionBlocks)
	}()

	wg.Wait()

	return &models.ExamQuestion{
		ID: id,
		Answer: parsed.answer,
		Blocks: taskBlocks,
		SolutionBlocks: solutionBlocks,
	}, nil
}

