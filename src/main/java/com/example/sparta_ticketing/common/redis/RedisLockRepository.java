package com.example.sparta_ticketing.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisLockRepository {

    private final StringRedisTemplate redisTemplate;
    private static final String LOCK_VALUE = "LOCK_VALUE";
    private static final long LOCK_EXPIRE = 3L;

    public Boolean tryLock(String key) {
        return redisTemplate.
                opsForValue()
                .setIfAbsent(key, LOCK_VALUE, Duration.ofSeconds(LOCK_EXPIRE));
    }

    public void releaseLock(String key) {
        redisTemplate.delete(key);
    }
}
