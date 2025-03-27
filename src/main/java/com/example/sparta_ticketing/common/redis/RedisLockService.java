package com.example.sparta_ticketing.common.redis;

import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RedisLockService{
    private final RedissonClient redissonClient;

    public  <T> T executeWithLock(String lockKey, Supplier<T> supplier){
        RLock lock = redissonClient.getFairLock(lockKey);
        boolean isLock = false;

        try {
            isLock = lock.tryLock(3, 5, TimeUnit.SECONDS);

            if (!isLock) {
                throw new InvalidRequestException("요청이 너무 많습니다. 잠시 후 다시 시도해주세요.");
            }

            return supplier.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중 인터럽트 발생", e);
        } finally {
            if (isLock && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
