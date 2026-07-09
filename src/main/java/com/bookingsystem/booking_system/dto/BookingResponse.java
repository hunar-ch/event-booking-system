package com.bookingsystem.booking_system.dto;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id, Long seatId, String seatNumber, Long userId,
        String status, LocalDateTime bookedAt, LocalDateTime cancellableUntil
) {}
