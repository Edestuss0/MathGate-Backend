package router

import (
	"database/sql"
	"os"
	"github.com/gin-gonic/gin"
	appHandlers "mathgate/internal/controller/http/app"
	examUseCases "mathgate/internal/usecases/exam"
	examHandlers "mathgate/internal/controller/http/exam"
)

type UseCases struct {
	DB *sql.DB
	ExamUseCase *examUseCases.ExamUseCase
	Dir string
}

func NewRouter(s UseCases) *gin.Engine {
		router := gin.New()
		router.Use(gin.Recovery())

		if os.Getenv("PROFILE") == "1" {
			router.Use(DiagnosticsMiddleware(s.DB))
		}
		RegisterApiRoutes(router, s)
		router.Static("/api/images", s.Dir + "/images")
		return router
}

func RegisterGeneralRoutes(router *gin.Engine) {
	router.GET("/health", appHandlers.HealthHandler)
}

func RegisterApiRoutes(router *gin.Engine, s UseCases) {
	RegisterExamRoutes(router, s)
	RegisterGeneralRoutes(router)
}

func RegisterExamRoutes(router *gin.Engine, s UseCases) {
	router.GET("/api/exam", examHandlers.GetQuestion(s.ExamUseCase))
	router.GET("/api/exam/themes", examHandlers.GetThemes(s.ExamUseCase))
}