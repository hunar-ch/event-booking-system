package com.bookingsystem.booking_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventCreateRequest(
        @NotBlank String name,
        String description,
        @NotNull LocalDateTime eventTime,
        @NotBlank String venue
) {}

