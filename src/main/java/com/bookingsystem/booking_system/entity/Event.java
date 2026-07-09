package com.bookingsystem.booking_system.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "events") @Data
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private LocalDateTime eventTime;
    private String venue;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>();
}
