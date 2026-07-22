package main

import (
	"log/slog"
	"mathgate/internal/adapter/config"
	examJson "mathgate/internal/adapter/json/exam"
	examRepo "mathgate/internal/adapter/postgres/exam"
	"mathgate/internal/adapter/postgres/storage"
	examSource "mathgate/internal/adapter/scraper/exam"
	examRedis "mathgate/internal/adapter/redis/exam"
	router "mathgate/internal/controller/http"
	examUsecase "mathgate/internal/usecases/exam"
	redis "mathgate/internal/adapter/redis"

	"github.com/joho/godotenv"
)

func main() {
	if err := godotenv.Load(); err != nil {
		slog.Info("No .env file found")
	}
	cfg, err := config.Load()
	if err != nil {
		slog.Error("Can't load config")
		return
	}

	store, err := storage.New(cfg.DbPath)
	if err != nil {
		slog.Error("Failed to load db", err)
		return
	}
	defer store.Close()

	redis, err := redis.New(cfg.RedisAddress, cfg.RedisPassword)
	if err != nil {
		slog.Error("failed to connect to redis", err)
		return
	}
	defer redis.Client.Close()

	examRs := examRedis.New(redis.Client)
	examRepo := examRepo.New(store.DB)
	examSrc := examSource.New(cfg.ExamSourceBaseURL, cfg.DataPath)
	examJs := examJson.New(cfg.DataPath)
	
	examUC := examUsecase.NewExamUseCase(examRepo, examSrc, *examJs, *examRs)
	
	r := router.NewRouter(router.UseCases{
		ExamUseCase: examUC,
		DB: store.DB,
		Dir: cfg.DataPath,
	})

	slog.Info("server listening", "addr", cfg.Port)
	if err := r.Run(cfg.Port); err != nil {
		slog.Error("server failed", "error", err)
	}

}