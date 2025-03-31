package com.example.sparta_ticketing.common.config;

import com.example.sparta_ticketing.common.redis.RedisViewCountService;
import com.example.sparta_ticketing.domain.auth.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ViewCountAspect {

    private final RedisViewCountService viewCountService;

    @Around("@annotation(com.example.sparta_ticketing.common.config.ViewCountAop) && args(showId,authUser)")
    public Object countView(ProceedingJoinPoint joinPoint, Long showId, AuthUser authUser) throws Throwable {
        Object result = joinPoint.proceed();
        viewCountService.increaseViewCount(showId, authUser.getId());
        return result;
    }
}