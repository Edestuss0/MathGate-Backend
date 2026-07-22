package source

import (
	"context"
	models "mathgate/internal/domain/exam"
)

type ExamSource interface {
	FetchQuestion(ctx context.Context, id int) (*models.ExamQuestion, error)
}