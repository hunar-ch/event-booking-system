package com.bookingsystem.booking_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users") @Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String passwordHash;
    private String phone;
    @Enumerated(EnumType.STRING)
    private Role role; // enum: USER, ADMIN
    private LocalDateTime createdAt = LocalDateTime.now();
}

