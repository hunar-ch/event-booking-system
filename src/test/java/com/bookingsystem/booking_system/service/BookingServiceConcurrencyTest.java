package com.bookingsystem.booking_system.service;

import com.bookingsystem.booking_system.entity.Event;
import com.bookingsystem.booking_system.entity.Seat;
import com.bookingsystem.booking_system.exception.SeatAlreadyBookedException;
import com.bookingsystem.booking_system.repository.EventRepository;
import com.bookingsystem.booking_system.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookingServiceConcurrencyTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SeatRepository seatRepository;

    private Long seatId;

    @BeforeEach
    void setUp() {
        // Create a fresh event and seat before each test run
        Event event = new Event();
        event.setName("Concurrency Test Event");
        event.setDescription("Test");
        event.setEventTime(LocalDateTime.now().plusDays(1));
        event.setVenue("Test Venue");
        Event savedEvent = eventRepository.save(event);

        Seat seat = new Seat();
        seat.setEvent(savedEvent);
        seat.setSeatNumber("TEST-A1");
        seat.setPrice(java.math.BigDecimal.valueOf(100));
        seat.setBooked(false);
        Seat savedSeat = seatRepository.save(seat);

        this.seatId = savedSeat.getId();
    }

    @Test
    void onlyOneBookingSucceedsWhenMultipleUsersRaceForSameSeat() throws InterruptedException {
        int numberOfUsers = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);
        CountDownLatch readyLatch = new CountDownLatch(numberOfUsers);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfUsers);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        for (int i = 1; i <= numberOfUsers; i++) {
            long userId = i;
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await(); // all threads wait here, then fire together
                    bookingService.bookSeat(userId, seatId);
                    successCount.incrementAndGet();
                } catch (SeatAlreadyBookedException e) {
                    conflictCount.incrementAndGet();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();          // wait until all threads are ready
        startLatch.countDown();      // release them all at once
        doneLatch.await(10, TimeUnit.SECONDS); // wait for all to finish
        executor.shutdown();

        assertEquals(1, successCount.get(), "Exactly one booking should succeed");
        assertEquals(numberOfUsers - 1, conflictCount.get(), "All other attempts should be rejected");
    }
}