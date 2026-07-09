package com.bookingsystem.booking_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SeatCreateRequest(
        @NotBlank String seatNumber,
        @NotNull BigDecimal price
) {}
