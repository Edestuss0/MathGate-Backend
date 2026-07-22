package theme

import (
	models "mathgate/internal/domain/exam"
)

type ThemeCatalog interface {
	Random(examType string) (*models.ThemeQuestion, error)
	ByThemeNumber(examType string, themeNumber int) (*models.ThemeQuestion, error)
	Themes(examType string) ([]models.ExamTheme, error)
}