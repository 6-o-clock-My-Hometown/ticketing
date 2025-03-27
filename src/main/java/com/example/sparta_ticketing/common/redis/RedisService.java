package com.example.sparta_ticketing.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public void set(String key, String value){
        redisTemplate.opsForValue().set(key,value);
    }

    public String get(String key){return redisTemplate.opsForValue().get(key);}

    public Long increment(String key){
        return redisTemplate.opsForValue().increment(key);
    }

    public Long decrement(String key){
        return redisTemplate.opsForValue().decrement(key);
    }

    public void setWithTtl(String key, String value,  Long ttl, TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key,value, ttl, timeUnit);
    }

    public Boolean exists(String key){
        return redisTemplate.hasKey(key);
    }

    public void delete(Set<String> key){redisTemplate.delete(key);}

    public Set<String> keys(String pattern){return redisTemplate.keys(pattern);}

    public Long getTtlHour(LocalDateTime endDateTime){
        // 현재시간
        LocalDateTime now = LocalDateTime.now();

        // ttl 계산
        return Duration.between(now, endDateTime).toHours();
    }

    public Long getTtlSecond(String key, LocalDateTime endDateTime){
        // 현재시간
        LocalDateTime now = LocalDateTime.now();

        // ttl 계산
        return Duration.between(now, endDateTime).getSeconds();
    }

    public Long getTtlMinute(String key, LocalDateTime endDateTime){
        // 현재시간
        LocalDateTime now = LocalDateTime.now();

        // ttl 계산
        return Duration.between(now, endDateTime).toMinutes();
    }
}
