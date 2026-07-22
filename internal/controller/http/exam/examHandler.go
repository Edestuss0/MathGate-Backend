package handlers

import (
	"log/slog"
	eu "mathgate/internal/usecases/exam"
	"strconv"

	"github.com/gin-gonic/gin"
)

func GetQuestion(examUseCase *eu.ExamUseCase) gin.HandlerFunc {
	return func(c *gin.Context) {
		examType := c.Query("type")
		if examType == "" {
			examType = "oge"
		}
		number := c.Query("number")
		if number == "" {
			result, err := examUseCase.GetRandomQuestion(c.Request.Context(), examType)
			if err != nil || result == nil {
				c.JSON(404, "Cannot find the question")
				slog.Error("error", err)
				return 
			}
			c.JSON(200, result)
		} else {
			theme, err := strconv.Atoi(number)
			if err != nil {
				c.JSON(401, "Incorrect theme number input")
				return
			}
			result, err := examUseCase.GetQuestionByTheme(c.Request.Context(), examType, theme)
			if err != nil || result == nil {
				c.JSON(404, "Cannot find the question")
				slog.Error("error", err)
				return
			}
			c.JSON(200, result)
		}
	}
}

func GetThemes(examUseCase *eu.ExamUseCase) gin.HandlerFunc {
	return func(c *gin.Context) {
		examType := c.Query("type")
		if examType == "" {
			examType = "oge"
		}
		themes, err := examUseCase.GetThemes(c.Request.Context(), examType)
		if err != nil || themes == nil {
			c.JSON(404, "Cannot find themes")
			return
		}
		c.JSON(200, themes)
	}
}