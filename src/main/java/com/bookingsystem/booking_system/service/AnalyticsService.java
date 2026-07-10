package com.bookingsystem.booking_system.service;

import com.bookingsystem.booking_system.dto.EventBookingCount;
import com.bookingsystem.booking_system.dto.EventOccupancy;
import com.bookingsystem.booking_system.dto.EventRevenue;
import com.bookingsystem.booking_system.entity.EventPopularity;
import com.bookingsystem.booking_system.exception.ResourceNotFoundException;
import com.bookingsystem.booking_system.repository.BookingRepository;
import com.bookingsystem.booking_system.repository.EventRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class AnalyticsService {

    private static final double POPULAR_THRESHOLD = 70.0;
    private static final double QUIET_THRESHOLD = 30.0;

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    public AnalyticsService(EventRepository eventRepository, BookingRepository bookingRepository) {
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
    }

    public EventOccupancy getOccupancy(Long eventId) {
        List<Object[]> results = eventRepository.findOccupancyStats(eventId);
        if (results.isEmpty()) {
            throw new ResourceNotFoundException("Event not found: " + eventId);
        }

        Object[] outer = results.get(0);
        Object[] row = (outer.length == 1 && outer[0] instanceof Object[])
                ? (Object[]) outer[0]
                : outer;

        String eventName = (String) row[0];
        Long totalSeats = ((Number) row[1]).longValue();
        Long bookedSeats = ((Number) row[2]).longValue();
        Double occupancyPercent = row[3] != null ? ((Number) row[3]).doubleValue() : 0.0;

        EventPopularity popularity = classify(occupancyPercent);

        return new EventOccupancy(eventName, totalSeats, bookedSeats, occupancyPercent, popularity);
    }

    private EventPopularity classify(double occupancyPercent) {
        if (occupancyPercent >= POPULAR_THRESHOLD) {
            return EventPopularity.POPULAR;
        } else if (occupancyPercent <= QUIET_THRESHOLD) {
            return EventPopularity.QUIET;
        } else {
            return EventPopularity.MODERATE;
        }
    }


    public List<EventBookingCount> getMostBookedEvents() {
        return eventRepository.findMostBookedEvents().stream()
                .map(row -> new EventBookingCount((String) row[0], ((Number) row[1]).longValue()))
                .toList();
    }


    public List<EventRevenue> getRevenueByEvent() {
        return bookingRepository.findRevenueByEvent().stream()
                .map(row -> new EventRevenue((String) row[0], ((Number) row[1]).doubleValue()))
                .toList();
    }

    public List<EventOccupancy> getAllOccupancies() {
        return eventRepository.findAll().stream()
                .map(event -> getOccupancy(event.getId()))
                .toList();
    }
}