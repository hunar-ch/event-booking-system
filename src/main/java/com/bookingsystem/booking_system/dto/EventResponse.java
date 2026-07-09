package com.bookingsystem.booking_system.dto;

import java.time.LocalDateTime;

public record EventResponse(
        Long id, String name, String description,
        LocalDateTime eventTime, String venue
) {}
