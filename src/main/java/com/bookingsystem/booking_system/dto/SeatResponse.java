package com.bookingsystem.booking_system.dto;

import java.math.BigDecimal;

public record SeatResponse(
        Long id, String seatNumber, BigDecimal price, boolean booked
) {}
