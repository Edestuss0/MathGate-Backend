package redis

import (
	"context"
	"time"

	"github.com/redis/go-redis/v9"
)

type Redis struct {
	Client *redis.Client
}

func New(address string, password string) (*Redis, error) {
	rdb := redis.NewClient(&redis.Options{
        Addr: address,
        Password: password,
        DB: 0,
	})
	ctx, cancel := context.WithTimeout(context.Background(), 10 * time.Second)
	defer cancel()
	err := rdb.Ping(ctx).Err()
	if err != nil {
		return nil, err
	}
	return &Redis{
		Client: rdb,
	}, nil
}