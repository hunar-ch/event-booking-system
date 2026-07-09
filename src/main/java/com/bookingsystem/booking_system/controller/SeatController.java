package com.bookingsystem.booking_system.controller;

import com.bookingsystem.booking_system.dto.SeatCreateRequest;
import com.bookingsystem.booking_system.dto.SeatResponse;
import com.bookingsystem.booking_system.service.SeatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/seats")
public class SeatController {
    private SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping
    public ResponseEntity<SeatResponse> addSeat(@PathVariable Long eventId,
                                                @Valid @RequestBody SeatCreateRequest req) {
        return ResponseEntity.ok(seatService.addSeat(eventId, req));
    }

    @GetMapping
    public ResponseEntity<List<SeatResponse>> getSeats(@PathVariable Long eventId) {
        return ResponseEntity.ok(seatService.getSeatsForEvent(eventId));
    }
}
