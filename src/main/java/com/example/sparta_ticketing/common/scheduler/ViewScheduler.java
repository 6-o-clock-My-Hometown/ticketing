package com.example.sparta_ticketing.common.scheduler;

import com.example.sparta_ticketing.common.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ViewScheduler {

    private final RedisService redisService;

    @Scheduled(cron = "0 0 0 * * *")
    public void viewCountResetScheduler(){
        Set<String> keys = redisService.keys("viewCount:*");
        if(keys!=null&& !keys.isEmpty()){
            redisService.delete(keys);
        }

    }

    @Scheduled(cron = "0 0 0 * * *")
    public void viewUserResetScheduler(){
        Set<String> keys = redisService.keys("view:*");
        if(keys!=null&& !keys.isEmpty()){
            redisService.delete(keys);
        }
    }
}
