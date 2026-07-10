package com.bookingsystem.booking_system.controller;

import com.bookingsystem.booking_system.dto.EventBookingCount;
import com.bookingsystem.booking_system.dto.EventOccupancy;
import com.bookingsystem.booking_system.dto.EventRevenue;
import com.bookingsystem.booking_system.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/most-booked")
    public ResponseEntity<List<EventBookingCount>> mostBooked() {
        return ResponseEntity.ok(analyticsService.getMostBookedEvents());
    }

    @GetMapping("/occupancy/{eventId}")
    public ResponseEntity<EventOccupancy> occupancy(@PathVariable Long eventId) {
        return ResponseEntity.ok(analyticsService.getOccupancy(eventId));
    }

    @GetMapping("/revenue")
    public ResponseEntity<List<EventRevenue>> revenue() {
        return ResponseEntity.ok(analyticsService.getRevenueByEvent());
    }

    @GetMapping("/occupancy")
    public ResponseEntity<List<EventOccupancy>> allOccupancies() {
        return ResponseEntity.ok(analyticsService.getAllOccupancies());
    }
}