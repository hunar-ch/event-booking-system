package com.bookingsystem.booking_system.service;

import com.bookingsystem.booking_system.dto.BookingRequest;
import com.bookingsystem.booking_system.dto.BookingResponse;
import com.bookingsystem.booking_system.entity.*;
import com.bookingsystem.booking_system.exception.ResourceNotFoundException;
import com.bookingsystem.booking_system.exception.SeatAlreadyBookedException;
import com.bookingsystem.booking_system.repository.BookingRepository;
import com.bookingsystem.booking_system.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;

    public BookingService(SeatRepository seatRepository, BookingRepository bookingRepository) {
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream().map(this::toResponse).toList();
    }

    public BookingResponse getBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
        return toResponse(booking);
    }

    @Transactional
    public BookingResponse bookSeat(BookingRequest req) {
        // Locks the seat, any other concurrent transaction trying to
        // book the same seat will be blocked until we commit/rollback
        Seat seat = seatRepository.findByIdForUpdate(req.seatId())
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found: " + req.seatId()));

        // Throwing an exception if seat is already booked
        // causing a rollback
        if (seat.isBooked()) {
            throw new SeatAlreadyBookedException("Seat " + seat.getSeatNumber() + " is already booked");
        }

        // Mark seat booked and save
        seat.setBooked(true);
        seatRepository.save(seat);

        // Create a booking record
        Booking booking = new Booking();
        booking.setSeat(seat);
        booking.setUserId(req.userId());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCancellableUntil(LocalDateTime.now().plusHours(24)); //cancellation window of 24 hours from booking
        Booking saved = bookingRepository.save(booking);

        return toResponse(saved);
        // Transaction commits here and the lock is released
        // Other waiting transactions proceed and now find the seat booked
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking already cancelled");
        }
        if (LocalDateTime.now().isAfter(booking.getCancellableUntil())) {
            throw new IllegalStateException("Cancellation window has expired");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Seat seat = booking.getSeat();
        seat.setBooked(false);
        seatRepository.save(seat);
        Booking saved = bookingRepository.save(booking);

        return toResponse(saved);
    }

    private BookingResponse toResponse(Booking b) {
        return new BookingResponse(
                b.getId(), b.getSeat().getId(), b.getSeat().getSeatNumber(),
                b.getUserId(), b.getStatus().name(), b.getBookedAt(), b.getCancellableUntil()
        );
    }
}