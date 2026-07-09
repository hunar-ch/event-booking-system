package com.bookingsystem.booking_system.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    //
    private Long userId;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime bookedAt = LocalDateTime.now();
    private LocalDateTime cancellableUntil;

    // getters and setters
    public Long getId() { return id; }
    public Seat getSeat() { return seat; }
    public void setSeat(Seat seat) { this.seat = seat; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public LocalDateTime getBookedAt() { return bookedAt; }
    public LocalDateTime getCancellableUntil() { return cancellableUntil; }
    public void setCancellableUntil(LocalDateTime cancellableUntil) { this.cancellableUntil = cancellableUntil; }
}