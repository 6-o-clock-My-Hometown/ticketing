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
            isLock = lock.tryLock(100, 500, TimeUnit.MILLISECONDS);
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


    public <T> T reserveSeatWithPessimisticLock(String lockKey, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 무조건 락 잡을 때까지 대기, 100ms 동안 보유
            lock.lock(100, TimeUnit.MILLISECONDS);

            // 락 잡고 비즈니스 로직 실행
            return supplier.get();

        } finally {
            // 현재 쓰레드가 락을 보유하고 있으면 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
