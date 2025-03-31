package com.example.sparta_ticketing.common.redis;

import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.repository.SeatRepository;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.repository.ShowRepository;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisKeyExpirationListner extends KeyExpirationEventMessageListener {
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final RedisService redisService;

    public RedisKeyExpirationListner(ShowRepository showRepository,
                                     SeatRepository seatRepository,
                                     RedisService redisService,
                                     RedisMessageListenerContainer redisMessageListenerContainer) {
        super(redisMessageListenerContainer);
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
        this.redisService = redisService;
    }

    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody()); //message는 body와 getChannel이 key만료 이벤트에 대한 정보가 byte배열로 담겨있음. body에는 reserve:start:{showId} 형식으로 되어있다.
        if (expiredKey.startsWith("reserve:start:show:")) {
            Long showId = Long.parseLong(expiredKey.split(":")[3]);

            Show show = showRepository.findById(showId).orElseThrow();
            List<Seat> seats = seatRepository.findByShowId(show.getId()).orElseThrow();

            for (Seat seat : seats) {
                String seatKey = "ticket:show:" + showId + ":seat:" + seat.getId();
                redisService.set(seatKey, String.valueOf(seat.getCount()));
                redisService.expire(seatKey, show.getReservationEndDate());
            }
        }
    }
}
