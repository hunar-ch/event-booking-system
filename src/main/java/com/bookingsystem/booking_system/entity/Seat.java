package com.bookingsystem.booking_system.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity @Table(name = "seats") @Data
public class Seat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "event_id")
    private Event event;

    private String seatNumber;
    private BigDecimal price;
    private boolean booked = false;

    @Version
    private Long version; // handy later even if you use pessimistic locking for booking
}
