package com.example.sparta_ticketing.domain.ticket.service;

import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import com.example.sparta_ticketing.common.redis.RedisService;
import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.enums.SeatEnum;
import com.example.sparta_ticketing.domain.seat.repository.SeatRepository;
import com.example.sparta_ticketing.domain.show.dto.request.CreateShowRequestDto;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.enums.Category;
import com.example.sparta_ticketing.domain.show.enums.Region;
import com.example.sparta_ticketing.domain.show.repository.ShowRepository;
import com.example.sparta_ticketing.domain.ticket.dto.request.CreateSeatReservationRequest;
import com.example.sparta_ticketing.domain.ticket.repository.TicketRepository;
import com.example.sparta_ticketing.domain.user.entity.User;
import com.example.sparta_ticketing.domain.user.enums.UserRole;
import com.example.sparta_ticketing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class TicketServiceWithoutLockTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TicketRepository ticketRepository;


    @BeforeEach
    void setUp() {
        // 좌석수 50개 가정
        redisService.set("show:1:seat:1", "50");
    }

    @Test
    void 동시예매시_50명만_가능하도록_제어() throws InterruptedException{
        User user = new User(
                "test@example.com",
                "password",
                "테스트유저",
                "010-1234-5678",
                "1995-05-10",
                UserRole.ROLE_USER);

        userRepository.save(user);

        Show show = new Show(
                new CreateShowRequestDto("공연",
                        Category.MUSICAL,
                        "공연내용",
                        Region.SEOUL,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(1)
                ),
                50,
                user
        );

        showRepository.save(show);

        Seat seat = new Seat(show, SeatEnum.VIP, 50, 10000);
        seatRepository.save(seat);

        int reserveCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(reserveCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();


        for (int i = 0; i < reserveCount; i++) {
            final int userId = i;

            executorService.submit(() -> {
                try {
                    CreateSeatReservationRequest request =
                            new CreateSeatReservationRequest(seat.getId(), show.getId());

                    ticketService.reserveSeat(user.getId(), request);
                    successCount.incrementAndGet();


                } catch (InvalidRequestException e) {
                    failCount.incrementAndGet(); // 예매 실패 (매진 등)
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        System.out.println("예매 성공한 사용자 수: " + successCount.get());
        System.out.println("예매 실패한 사용자 수: " + failCount.get());
        assertEquals(50, successCount.get());
        assertEquals(50, ticketRepository.count());

    }


    @Test
    void 동시성제어없이_동시예매시_실패() throws InterruptedException{
        User user = new User(
                "test@example.com",
                "password",
                "테스트유저",
                "010-1234-5678",
                "1995-05-10",
                UserRole.ROLE_USER);

        userRepository.save(user);

        Show show = new Show(
                new CreateShowRequestDto("공연",
                        Category.MUSICAL,
                        "공연내용",
                        Region.SEOUL,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(1)
                ),
                50,
                user
        );

        showRepository.save(show);

        Seat seat = new Seat(show, SeatEnum.VIP, 50, 10000);
        seatRepository.save(seat);

        int reserveCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(reserveCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();


        for (int i = 0; i < reserveCount; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    CreateSeatReservationRequest request =
                            new CreateSeatReservationRequest(seat.getId(), show.getId());

                    ticketService.reserveSeatWithoutLock(user.getId(), request);
                    successCount.incrementAndGet();


                } catch (InvalidRequestException e) {
                    failCount.incrementAndGet(); // 예매 실패 (매진 등)
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        System.out.println("예매 성공한 사용자 수: " + successCount.get());
        System.out.println("예매 실패한 사용자 수: " + failCount.get());
    }

}