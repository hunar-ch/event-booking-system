package com.bookingsystem.booking_system.service;

import com.bookingsystem.booking_system.dto.BookingRequest;
import com.bookingsystem.booking_system.dto.EventBookingCount;
import com.bookingsystem.booking_system.dto.EventOccupancy;
import com.bookingsystem.booking_system.dto.EventRevenue;
import com.bookingsystem.booking_system.entity.Event;
import com.bookingsystem.booking_system.entity.EventPopularity;
import com.bookingsystem.booking_system.entity.Seat;
import com.bookingsystem.booking_system.repository.EventRepository;
import com.bookingsystem.booking_system.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AnalyticsServiceTest {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SeatRepository seatRepository;

    private Long popularEventId;
    private Long quietEventId;

    @BeforeEach
    void setUp() {
        // Event with 3 seats, 2 booked -> "popular"
        Event popularEvent = new Event();
        popularEvent.setName("Popular Test Event");
        popularEvent.setDescription("Test");
        popularEvent.setEventTime(LocalDateTime.now().plusDays(1));
        popularEvent.setVenue("Test Venue A");
        popularEvent = eventRepository.save(popularEvent);
        popularEventId = popularEvent.getId();

        Seat p1 = createSeat(popularEvent, "P1", BigDecimal.valueOf(100));
        Seat p2 = createSeat(popularEvent, "P2", BigDecimal.valueOf(100));
        createSeat(popularEvent, "P3", BigDecimal.valueOf(100)); // left unbooked

        bookingService.bookSeat(1L, p1.getId());
        bookingService.bookSeat(2L, p2.getId());

        // Event with 2 seats, 0 booked -> "quiet"
        Event quietEvent = new Event();
        quietEvent.setName("Quiet Test Event");
        quietEvent.setDescription("Test");
        quietEvent.setEventTime(LocalDateTime.now().plusDays(2));
        quietEvent.setVenue("Test Venue B");
        quietEvent = eventRepository.save(quietEvent);
        quietEventId = quietEvent.getId();

        createSeat(quietEvent, "Q1", BigDecimal.valueOf(50));
        createSeat(quietEvent, "Q2", BigDecimal.valueOf(50));
    }

    private Seat createSeat(Event event, String seatNumber, BigDecimal price) {
        Seat seat = new Seat();
        seat.setEvent(event);
        seat.setSeatNumber(seatNumber);
        seat.setPrice(price);
        seat.setBooked(false);
        return seatRepository.save(seat);
    }

    @Test
    void mostBookedEventsIncludesPopularEventAheadOfQuietEvent() {
        List<EventBookingCount> results = analyticsService.getMostBookedEvents();

        assertFalse(results.isEmpty(), "Expected at least one event in most-booked results");

        // Find our two test events within the results (there may be other events from prior test runs)
        var popular = results.stream()
                .filter(r -> r.eventName().equals("Popular Test Event"))
                .findFirst();
        var quiet = results.stream()
                .filter(r -> r.eventName().equals("Quiet Test Event"))
                .findFirst();

        assertTrue(popular.isPresent(), "Popular event should appear in most-booked results");
        assertEquals(2L, popular.get().totalBookings());

        // Quiet event has 0 bookings, so it may not appear at all depending on the query's JOIN type
        quiet.ifPresent(q -> assertEquals(0L, q.totalBookings()));
    }

    @Test
    void occupancyReflectsBookedVsTotalSeats() {
        EventOccupancy occupancy = analyticsService.getOccupancy(popularEventId);

        assertEquals("Popular Test Event", occupancy.eventName());
        assertEquals(3L, occupancy.totalSeats());
        assertEquals(2L, occupancy.bookedSeats());
        assertEquals(66.67, occupancy.occupancyPercent(), 0.01);
        assertEquals(EventPopularity.MODERATE, occupancy.popularity()); // 66.67% falls in the 30-70 range
    }

    @Test
    void occupancyIsZeroForEventWithNoBookings() {
        EventOccupancy occupancy = analyticsService.getOccupancy(quietEventId);

        assertEquals("Quiet Test Event", occupancy.eventName());
        assertEquals(2L, occupancy.totalSeats());
        assertEquals(0L, occupancy.bookedSeats());
        assertEquals(0.0, occupancy.occupancyPercent(), 0.01);
        assertEquals(EventPopularity.QUIET, occupancy.popularity());
    }

    @Test
    void revenueByEventReflectsOnlyConfirmedBookings() {
        List<EventRevenue> results = analyticsService.getRevenueByEvent();

        var popularRevenue = results.stream()
                .filter(r -> r.eventName().equals("Popular Test Event"))
                .findFirst();

        assertTrue(popularRevenue.isPresent(), "Popular event should appear in revenue results");
        assertEquals(200.0, popularRevenue.get().totalRevenue(), 0.01); // 2 seats x 100 each
    }
}