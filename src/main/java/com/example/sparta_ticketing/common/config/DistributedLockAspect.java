package com.example.sparta_ticketing.common.config;


import com.example.sparta_ticketing.common.redis.RedisLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;


@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class DistributedLockAspect {
    private final RedisLockService redisLockService;

    @Around("@annotation(distributedLock)")
    public Object applyLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock){
        //lockKey를 동적 파싱
        String lockKey = parseSpELKey(joinPoint, distributedLock.key());
        log.info(joinPoint.getSignature().getName());

        Supplier<Object> logic = () ->{
            try {
                return joinPoint.proceed();

            }catch (Throwable throwable){
                throw new RuntimeException(throwable);
            }
        };

        //실제 락 실행
        return redisLockService.executeWithLock(lockKey, logic);
    }

//    @Around("@annotation(pessimisticLock)")
//    public Object applyPessimisticLock(ProceedingJoinPoint joinPoint, PessimisticLock pessimisticLock){
//        //lockKey를 동적 파싱
//        String lockKey = parseSpELKey(joinPoint, pessimisticLock.key());
//        log.info(joinPoint.getSignature().getName());
//
//        Supplier<Object> logic = () ->{
//            try {
//                return joinPoint.proceed();
//
//            }catch (Throwable throwable){
//                throw new RuntimeException(throwable);
//            }
//        };
//
//        //비관적 락 실행
//        return redisLockService.reserveSeatWithPessimisticLock(lockKey, logic);
//    }

    //spEl표현식을 실행하여 redis key 문자열 반환
    private String parseSpELKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 실행중인 메서드에 대한 정보를 객체로 반환
        String[] parameterNames = signature.getParameterNames(); // 메서드에 정의된 파라미터 이름 반환
        Object[] args = joinPoint.getArgs();

        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        ExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(keyExpression).getValue(context, String.class);
    }
}
