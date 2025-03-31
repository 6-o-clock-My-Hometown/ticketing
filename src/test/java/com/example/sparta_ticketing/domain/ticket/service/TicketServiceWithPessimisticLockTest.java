package com.example.sparta_ticketing.domain.ticket.service;

import com.example.sparta_ticketing.common.exception.InvalidRequestException;
import com.example.sparta_ticketing.common.security.JwtUtil;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@ActiveProfiles("test")
class TicketServiceWithPessimisticLockTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void 비관적락_동시성_제어_평균처리시간_테스트() throws InterruptedException {
        int repeatCount = 5;
        long totalElapsedTime = 0;
        int totalSuccess = 0;
        int totalFail = 0;

            // 초기화 (각 테스트마다 새로운 유저, 공연, 좌석 필요)
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

            int reserveCount = 100;
            ExecutorService executorService = Executors.newFixedThreadPool(1000);
            CountDownLatch latch = new CountDownLatch(reserveCount);
            AtomicInteger successCount = new AtomicInteger();
            AtomicInteger failCount = new AtomicInteger();

            long startTime = System.currentTimeMillis();

            for (int i = 0; i < reserveCount; i++) {
                executorService.submit(() -> {
                    try {
                        CreateSeatReservationRequest request =
                                new CreateSeatReservationRequest(seat.getId(), show.getId());
                        ticketService.reserveSeatWithPessimisticLock(user.getId(), request);
                        successCount.incrementAndGet();
                    } catch (InvalidRequestException e) {
                        failCount.incrementAndGet();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            long endTime = System.currentTimeMillis();

            totalElapsedTime += endTime - startTime;;
            totalSuccess += successCount.get();
            totalFail += failCount.get();

            // 테스트 종료 후 저장된 티켓 초기화 (안하면 중복으로 누적됨)
            ticketRepository.deleteAll();
            seatRepository.deleteAll();
            showRepository.deleteAll();
            userRepository.deleteAll();

        System.out.println("----- 평균 결과 -----");
        System.out.println("평균 처리 시간: " + totalElapsedTime+ "ms");
        System.out.println("평균 성공 수: " + totalSuccess );
        System.out.println("평균 실패 수: " + totalFail);
    }
}