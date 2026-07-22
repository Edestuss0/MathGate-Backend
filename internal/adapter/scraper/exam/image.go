package scraper

import (
	"context"
	"crypto/sha256"
	"encoding/hex"
	"errors"
	"fmt"
	"io"
	"log/slog"
	"net/http"
	"os"
	"path/filepath"
	"sync"

	models "mathgate/internal/domain/exam"
)

func downloadImagesForBlocks(ctx context.Context, client *http.Client, dir string, blocks []models.ExamBlock) []models.ExamBlock {
	result := make([]models.ExamBlock, len(blocks))
	copy(result, blocks)

	err := os.MkdirAll(filepath.Join(dir, "images"), 0755)
	if err != nil {
			return result
	}

	var wg sync.WaitGroup
	for i, b := range blocks {
		if b.BlockType != "IMAGE" && b.BlockType != "FORMULA" {
			continue
		}
		wg.Add(1)
		go func(i int, imageURL string) {
			defer wg.Done()
			data, mime, err := DownloadImage(ctx, client, imageURL)
			if err != nil {
				slog.Error("error by download images", err)
				return
			}
			
			ext := Extension(mime)

			fileName := FileName(data, ext)

			path := filepath.Join(dir, "images", fileName)

			if _, err := os.Stat(path); errors.Is(err, os.ErrNotExist) {
				if err := os.WriteFile(path, data, 0644); err != nil {
					slog.Error("image save failed", err)
					return 
				}
			}

			result[i].Content = "/images/" + fileName
		}(i, b.Content)
	}
	wg.Wait()

	return result
}

func FileName(data []byte, ext string) string {
    hash := sha256.Sum256(data)
    return hex.EncodeToString(hash[:]) + ext
}

func SaveImage(path string, data []byte) error {
    return os.WriteFile(path, data, 0644)
}

func Extension(contentType string) string {
    switch contentType {
    case "image/png":
        return ".png"

    case "image/jpeg":
        return ".jpg"

    case "image/webp":
        return ".webp"

    case "image/svg+xml":
        return ".svg"

    default:
        return ""
    }
}

func DownloadImage(ctx context.Context, client *http.Client, url string) ([]byte, string, error) {
    req, err := http.NewRequestWithContext(ctx, http.MethodGet, url, nil)
    if err != nil {
        return nil, "", err
    }

    resp, err := client.Do(req)
    if err != nil {
        return nil, "", err
    }
    defer resp.Body.Close()

    if resp.StatusCode != http.StatusOK {
        return nil, "", fmt.Errorf("status %d", resp.StatusCode)
    }

    data, err := io.ReadAll(resp.Body)
    if err != nil {
        return nil, "", err
    }

    contentType := resp.Header.Get("Content-Type")

    return data, contentType, nil
}