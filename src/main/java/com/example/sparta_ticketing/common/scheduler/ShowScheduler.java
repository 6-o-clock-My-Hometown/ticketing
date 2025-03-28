package com.example.sparta_ticketing.common.scheduler;

import com.example.sparta_ticketing.common.redis.RedisService;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ShowScheduler {

    private final RedisService redisService;
    private final ShowService showService;

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

    @Scheduled(cron = "0 * * * * *")
    public void checkTicketExpired(){
        List<Show> showList= showService.findExpiredShow();

        if(showList!=null && !showList.isEmpty()){
            for(Show show:showList){
                // 만료된 공연 비활성화
                showService.changeShowStatus(show.getId());

                // 좌석 정보 키 제거
                Set<String> seatKeys = redisService.keys("ticket:show:"+show.getId()+"*");
                redisService.delete(seatKeys);
            }
        }
    }


}
