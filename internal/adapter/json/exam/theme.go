package json

import (
	"encoding/json"
	"fmt"
	"math/rand"
	"os"
	"path/filepath"
	"sync"

	models "mathgate/internal/domain/exam"
)

type storagedTheme struct {
	Number int    `json:"number"`
	Label  string `json:"label"`
	Tasks  []int  `json:"tasks"`
}

type examTypeCatalog struct {
	themes         []storagedTheme
	taskToThemeIdx map[int]int
}

type JsonThemeCatalog struct {
	dir string

	mu    sync.RWMutex
	cache map[string]*examTypeCatalog
}

func New(dir string) *JsonThemeCatalog {
	return &JsonThemeCatalog{
		dir:   dir + "/exam",
		cache: make(map[string]*examTypeCatalog),
	}
}

func (c *JsonThemeCatalog) Random(examType string) (*models.ThemeQuestion, error) {
	et, err := c.load(examType)
	if err != nil {
		return nil, err
	}
	if len(et.themes) == 0 {
		return nil, fmt.Errorf("нет тем для экзамена %q", examType)
	}
	theme := et.themes[rand.Intn(len(et.themes))]
	if len(theme.Tasks) == 0 {
		return nil, fmt.Errorf("в теме %q нет задач", theme.Label)
	}
	taskID := theme.Tasks[rand.Intn(len(theme.Tasks))]

	return &models.ThemeQuestion{
		TaskID:      taskID,
		ThemeNumber: theme.Number,
		ThemeLabel:  theme.Label,
	}, nil
}

func (c *JsonThemeCatalog) GetQuestionByThemeNumber(examType string, themeNumber int) (*models.ThemeQuestion, error) {
	et, err := c.load(examType)
	if err != nil {
		return nil, err
	}
	for _, theme := range et.themes {
		if theme.Number != themeNumber {
			continue
		}
		if len(theme.Tasks) == 0 {
			return nil, fmt.Errorf("в теме %q нет задач", theme.Label)
		}
		taskID := theme.Tasks[rand.Intn(len(theme.Tasks))]
		return &models.ThemeQuestion{
			TaskID:      taskID,
			ThemeNumber: theme.Number,
			ThemeLabel:  theme.Label,
		}, nil
	}
	return nil, fmt.Errorf("тема %d не найдена для экзамена %q", themeNumber, examType)
}


func (c *JsonThemeCatalog) Themes(examType string) ([]models.ExamTheme, error) {
	et, err := c.load(examType)
	if err != nil {
		return nil, err
	}
	summaries := make([]models.ExamTheme, 0, len(et.themes))
	for _, t := range et.themes {
		summaries = append(summaries, models.ExamTheme{
			Number: t.Number,
			Label:  t.Label,
			Tasks:  len(t.Tasks),
		})
	}
	return summaries, nil
}


func (c *JsonThemeCatalog) load(examType string) (*examTypeCatalog, error) {
	c.mu.RLock()
	et, ok := c.cache[examType]
	c.mu.RUnlock()
	if ok {
		return et, nil
	}

	c.mu.Lock()
	defer c.mu.Unlock()

	if et, ok := c.cache[examType]; ok {
		return et, nil
	}

	path := filepath.Join(c.dir, examType+".json")
	data, err := os.ReadFile(path)
	if err != nil {
		return nil, fmt.Errorf("не найден каталог тем для экзамена %q: %w", examType, err)
	}

	var themes []storagedTheme
	if err := json.Unmarshal(data, &themes); err != nil {
		return nil, fmt.Errorf("некорректный каталог тем для экзамена %q: %w", examType, err)
	}

	taskToTheme := make(map[int]int, len(themes))
	for i, t := range themes {
		for _, taskID := range t.Tasks {
			taskToTheme[taskID] = i
		}
	}

	built := &examTypeCatalog{themes: themes, taskToThemeIdx: taskToTheme}
	c.cache[examType] = built
	return built, nil
}