package com.example.sparta_ticketing.common.redis;

import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RedisLockService {
    private final RedisLockRepository redisLockRepository;

    public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
        Boolean flag = redisLockRepository.tryLock(lockKey);

        if(!flag){
            throw new InvalidRequestException("현재 요청이 너무 많습니다. 잠시후 다시 시도해주세요");
        }

        try {
            return supplier.get();

        } finally {
            redisLockRepository.releaseLock(lockKey);
        }
    }
}
