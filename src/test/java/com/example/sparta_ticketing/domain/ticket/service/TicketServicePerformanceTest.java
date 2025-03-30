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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@ActiveProfiles("test")
public class TicketServicePerformanceTest {

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

    private static final int REPEAT_COUNT = 1;
    private static final int RESERVE_COUNT = 5000;
    private static final int THREAD_POOL_SIZE = 200;

    @Test
    void 예매_성능비교_비관적락_vs_분산락_락없을때() throws InterruptedException {
        System.out.println("========= 성능 테스트 시작 =========");

        List<Result> pessimisticResults = new ArrayList<>();
        List<Result> redisResults = new ArrayList<>();
        List<Result> noLockResults = new ArrayList<>();


        // Pessimistic Lock Test
        for (int i = 0; i < REPEAT_COUNT; i++) {
            User user = createUser("pessimistic_user" + i);
            Show show = createShow(user);
            Seat seat = createSeat(show);

//            Result result = runConcurrentTest(() -> {
//                CreateSeatReservationRequest req = new CreateSeatReservationRequest(seat.getId(), show.getId());
//                ticketService.reserveSeatWithPessimisticLock(user.getId(), req);
//            });

            Result result = runConcurrentTest(() -> {
                CreateSeatReservationRequest req = new CreateSeatReservationRequest(seat.getId(), show.getId());
                reserveWithSleep(user.getId(), req);
            });

            pessimisticResults.add(result);
            cleanup();
        }

        // Redis Lock Test
        for (int i = 0; i < REPEAT_COUNT; i++) {
            User user = createUser("redis_user" + i);
            Show show = createShow(user);
            Seat seat = createSeat(show);

            redisService.set("canReserve:show:" + show.getId(), "1");
            redisService.set("ticket:show:" + show.getId() + ":seat:" + seat.getId(), "50");

            Result result = runConcurrentTest(() -> {
                CreateSeatReservationRequest req = new CreateSeatReservationRequest(seat.getId(), show.getId());
                ticketService.reserveSeat(user.getId(), req);
            });

            redisResults.add(result);
            cleanup();
        }

        // No Lock Test
        for (int i = 0; i < REPEAT_COUNT; i++) {
            User user = createUser("nolock_user" + i);
            Show show = createShow(user);
            Seat seat = createSeat(show);

            Result result = runConcurrentTest(() -> {
                CreateSeatReservationRequest req = new CreateSeatReservationRequest(seat.getId(), show.getId());
                ticketService.reserveSeatWithoutLock(user.getId(), req);
            });

            noLockResults.add(result);
            cleanup();
        }

        printSummary("비관적 락", pessimisticResults);
        printSummary("Redis 락", redisResults);
        printSummary("락없을때",  noLockResults);

        System.out.println("========= 성능 테스트 종료 =========");
    }

    private Result runConcurrentTest(Runnable task) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        CountDownLatch latch = new CountDownLatch(RESERVE_COUNT);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();

        long start = System.currentTimeMillis();

        for (int i = 0; i < RESERVE_COUNT; i++) {
            executor.submit(() -> {
                try {
                    task.run();
                    success.incrementAndGet();
                } catch (InvalidRequestException e) {
                    fail.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long end = System.currentTimeMillis();

        return new Result(end - start, success.get(), fail.get());
    }

    // 비관적락 충돌상황을 가정하기 위해 쓰레드 처리 속도를 감소
    private void reserveWithSleep(Long userId, CreateSeatReservationRequest req) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        ticketService.reserveSeatWithPessimisticLock(userId, req);
    }

    private User createUser(String email) {
        return userRepository.save(new User(email, "pw", "이름", "010", "1990-01-01", UserRole.ROLE_USER));
    }

    private Show createShow(User user) {
        return showRepository.save(new Show(
                new CreateShowRequestDto("공연", Category.MUSICAL, "내용", Region.SEOUL,
                        LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                        LocalDateTime.now(), LocalDateTime.now().plusDays(1)),
                50, user));
    }

    private Seat createSeat(Show show) {
        return seatRepository.save(new Seat(show, SeatEnum.VIP, 50, 10000));
    }

    private void cleanup() {
        ticketRepository.deleteAll();
        seatRepository.deleteAll();
        showRepository.deleteAll();
        userRepository.deleteAll();
    }

    private void printSummary(String label, List<Result> results) {
        System.out.println("===== " + label + " 요약 =====");

        long totalElapsed = 0;
        int totalSuccess = 0;
        int totalFail = 0;

        for (int i = 0; i < results.size(); i++) {
            Result r = results.get(i);
            totalElapsed += r.elapsedTime;
            totalSuccess += r.success;
            totalFail += r.fail;

            System.out.printf("[%s - %d회차] 시간: %dms | 성공: %d | 실패: %d\n",
                    label, i + 1, r.elapsedTime, r.success, r.fail);
        }

        int repeat = results.size();
        long avgTime = totalElapsed / repeat;
        double successRate = 100.0 * totalSuccess / (totalSuccess + totalFail);

        System.out.println("---- 평균 결과 ----");
        System.out.println("평균 처리 시간: " + avgTime + "ms");
        System.out.println("총 성공: " + totalSuccess + " / 총 실패: " + totalFail);
        System.out.printf("성공률: %.2f%%\n", successRate);
        System.out.println();
    }

    private static class Result {
        long elapsedTime;
        int success;
        int fail;

        public Result(long elapsedTime, int success, int fail) {
            this.elapsedTime = elapsedTime;
            this.success = success;
            this.fail = fail;
        }
    }
}
