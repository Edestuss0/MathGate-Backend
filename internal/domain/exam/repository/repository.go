package repo

import (
	"context"
	models "mathgate/internal/domain/exam"
)

type ExamRepository interface {
	GetRandomQuestion(examType string) (*models.ExamQuestion, error)
	GetQuestion(ctx context.Context, id int) (*models.ExamQuestion, error)
	Save(ctx context.Context, question *models.ExamQuestion, examType string) error
}