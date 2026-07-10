package com.bookingsystem.booking_system.dto;

import com.bookingsystem.booking_system.entity.EventPopularity;

public record EventOccupancy(
        String eventName,
        Long totalSeats,
        Long bookedSeats,
        Double occupancyPercent,
        EventPopularity popularity) {}