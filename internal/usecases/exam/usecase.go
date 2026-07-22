package usecase

import (
	"context"
	"fmt"
	"log/slog"
	json "mathgate/internal/adapter/json/exam"
	redis "mathgate/internal/adapter/redis/exam"
	source "mathgate/internal/adapter/scraper/exam"
	models "mathgate/internal/domain/exam"
	repo "mathgate/internal/domain/exam/repository"
	"time"
)

type ExamUseCase struct {
	repo repo.ExamRepository
	source source.HttpExamSource
	json json.JsonThemeCatalog
	redis redis.ExamRedis
}

func NewExamUseCase(repo repo.ExamRepository, src source.HttpExamSource, js json.JsonThemeCatalog, rd redis.ExamRedis) *ExamUseCase {
	return &ExamUseCase{ repo: repo, source: src, json: js, redis: rd,}
}

func (s *ExamUseCase) GetRandomQuestion(ctx context.Context, examType string) (*models.ExamQuestion, error) {
	task, err := s.json.Random(examType)
	if err != nil {
		return nil, err
	}

	redisCached, err := s.redis.GetQuestion(ctx, task.TaskID)
	if err == nil && redisCached != nil {
		fmt.Println("getted from redis")
		return redisCached, nil
	}

	cached, err := s.repo.GetQuestion(ctx, task.TaskID)
	if err == nil && cached != nil {
		return cached, nil
	}

	result, err := s.source.FetchQuestion(ctx, task.TaskID, examType)
	if err != nil {
		return nil, err
	}
	if result == nil {
		return nil, nil
	}

	go func() {
		ctx, cancel := context.WithTimeout(context.Background(), 10 * time.Second)
		defer cancel()
			
		err := s.repo.Save(ctx, &models.ExamQuestion{
			Answer: result.Answer,
			Blocks: result.Blocks,
			SolutionBlocks: result.SolutionBlocks,
			ThemeNumber: task.ThemeNumber,
			ThemeLabel: task.ThemeLabel,
			ID: task.TaskID,
		}, examType)
		if err != nil {
			slog.Error("failed to save to db", err)
		}
	} ()

	go func(q models.ExamQuestion) {
		ctx, cancel := context.WithTimeout(context.Background(), 10 * time.Second)
		defer cancel()

		err := s.redis.SaveQuestion(ctx, &q)
		if err != nil {
			slog.Error("failed to save to redis", err)
		}
		fmt.Println("saved to redis")
	}(*result)

	return &models.ExamQuestion{
		Answer: result.Answer,
		Blocks: result.Blocks,
		SolutionBlocks: result.SolutionBlocks,
		ThemeNumber: task.ThemeNumber,
		ThemeLabel: task.ThemeLabel,
		ID: task.TaskID,
	}, nil
}

func (s *ExamUseCase) GetQuestionByTheme(ctx context.Context, examType string, number int) (*models.ExamQuestion, error) {
	task, err := s.json.GetQuestionByThemeNumber(examType, number)
	fmt.Println(task.ThemeLabel)
	if err != nil {
		return nil, err
	}
	if task == nil {
		return nil, fmt.Errorf("Failed to get theme data")
	}

	redisCached, err := s.redis.GetQuestion(ctx, task.TaskID)
	if err == nil && redisCached != nil {
		fmt.Println("getted from redis")
		return redisCached, nil
	}

	cached, err := s.repo.GetQuestion(ctx, task.TaskID)
	if err == nil && cached != nil {
		return cached, nil
	}

	result, err := s.source.FetchQuestion(ctx, task.TaskID, examType)
	if err != nil {
		return nil, err
	}
	if result == nil {
		return nil, nil
	}

	go func()  {
		ctx, cancel := context.WithTimeout(context.Background(), 10 * time.Second)
		defer cancel()
		
		s.repo.Save(ctx, &models.ExamQuestion{
			Answer: result.Answer,
			Blocks: result.Blocks,
			SolutionBlocks: result.SolutionBlocks,
			ThemeNumber: task.ThemeNumber,
			ThemeLabel: task.ThemeLabel,
			ID: task.TaskID,
		}, examType)
	} ()

	go func(q models.ExamQuestion) {
		ctx, cancel := context.WithTimeout(context.Background(), 10 * time.Second)
		defer cancel()

		err := s.redis.SaveQuestion(ctx, &q)
		if err != nil {
			slog.Error("failed to save to redis", err)
		}
		fmt.Println("saved to redis")
	}(*result)

	return &models.ExamQuestion{
		Answer: result.Answer,
		Blocks: result.Blocks,
		SolutionBlocks: result.SolutionBlocks,
		ThemeNumber: task.ThemeNumber,
		ThemeLabel: task.ThemeLabel,
		ID: task.TaskID,
	}, nil
}

func (s *ExamUseCase) GetThemes(ctx context.Context, examType string) ([]models.ExamTheme, error) {
	redisCached, err := s.redis.GetThemes(ctx, examType)
	if err == nil && redisCached != nil {
		return redisCached, nil
	}
	theme, err := s.json.Themes(examType)
	if err != nil {
		return nil, err
	}
	if theme == nil {
		return nil, fmt.Errorf("Failed to get themes")
	}
	go func(examType string, t []models.ExamTheme) {
		err := s.redis.SaveThemes(ctx, t, examType)
		if err != nil {
			slog.Error("Failed to save theme to redis", err)
		}
	}(examType, theme)
	return theme, nil
}
