package config

import (
	"os"
)

type Config struct {
	Port string
	DbPath string
	DataPath string
	ExamSourceBaseURL string
	RedisAddress string
	RedisPassword string
}

func Load() (Config, error) {
	port := os.Getenv("PORT")
	if port == "" {
		port = ":8080"
	}
	dbPath := os.Getenv("DB_PATH")
	if dbPath == "" {
		dbPath = "postgres://mathgate:123456@localhost:5432/mathgate?sslmode=disable"
	}
	dataPath := os.Getenv("EXAM_THEME_PATH")
	if dataPath == "" {
		dataPath = "./data"
	}
	redisAddr := os.Getenv("REDIS_ADDRESS")
	if redisAddr == "" {
		redisAddr = "localhost:6379"
	}

	return Config{
		Port: port,
		DbPath: dbPath,
		DataPath: dataPath,
		ExamSourceBaseURL: os.Getenv("EXAM_SOURCE_BASE_URL"),
		RedisAddress: redisAddr,
		RedisPassword: os.Getenv("REDIS_PASSWORD"),
	}, nil
}