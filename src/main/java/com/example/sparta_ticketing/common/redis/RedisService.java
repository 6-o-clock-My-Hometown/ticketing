package com.example.sparta_ticketing.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

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

    public void setWithTtl(String key, String value,  Duration ttl){
        redisTemplate.opsForValue().set(key,value, ttl);
    }

    public Boolean exists(String key){
        return redisTemplate.hasKey(key);
    }

    public void delete(Set<String> key){redisTemplate.delete(key);}

    public Set<String> keys(String pattern){return redisTemplate.keys(pattern);}
}
