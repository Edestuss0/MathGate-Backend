package redis

import (
	"context"
	"encoding/json"
	"fmt"
	models "mathgate/internal/domain/exam"

	"github.com/redis/go-redis/v9"
)

type ExamRedis struct {
	client *redis.Client
}

func New(client *redis.Client) *ExamRedis {
	return &ExamRedis{client: client}
} 

func (s *ExamRedis) GetQuestion(ctx context.Context, id int) (*models.ExamQuestion, error) {
	result, err := s.client.Get(ctx, "question:" + string(id)).Bytes()
	if err != nil {
		return nil, err
	}
	var question *models.ExamQuestion

	err = json.Unmarshal(result, &question)
	if err != nil {
		return nil, err
	}

	return question, nil
}

func (s *ExamRedis) SaveQuestion(ctx context.Context, question *models.ExamQuestion) error {
	data, err := json.Marshal(question)
	if err != nil {
		return err
	}
	err = s.client.Set(ctx, "question:" + string(question.ID), data, 0).Err()
	if err != nil {
		return err
	}
	return nil
}

func (s *ExamRedis) GetThemes(ctx context.Context, examType string) ([]models.ExamTheme, error) {
	result, err := s.client.Get(ctx, "theme:" + examType).Bytes()
	if err != nil {
		return nil, err
	}
	var themes *[]models.ExamTheme

	err = json.Unmarshal(result, &themes)
	if err != nil {
		return nil, err
	}

	return *themes, nil
}

func (s *ExamRedis) SaveThemes(ctx context.Context, themes []models.ExamTheme, examType string) error {
	data, err := json.Marshal(themes)
	if err != nil {
		return err
	}
	if data == nil {
		return fmt.Errorf("Failed to save themes")
	}
	err = s.client.Set(ctx, "themes:" + examType, data, 0).Err()
	if err != nil {
		return err
	}
	return nil
}
