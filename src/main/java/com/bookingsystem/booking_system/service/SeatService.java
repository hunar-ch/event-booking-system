package com.bookingsystem.booking_system.service;

import com.bookingsystem.booking_system.dto.SeatCreateRequest;
import com.bookingsystem.booking_system.dto.SeatResponse;
import com.bookingsystem.booking_system.entity.Event;
import com.bookingsystem.booking_system.entity.Seat;
import com.bookingsystem.booking_system.exception.ResourceNotFoundException;
import com.bookingsystem.booking_system.repository.EventRepository;
import com.bookingsystem.booking_system.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatService {
    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;

    public SeatService(SeatRepository seatRepository, EventRepository eventRepository) {
        this.seatRepository = seatRepository;
        this.eventRepository = eventRepository;
    }

    public SeatResponse addSeat(Long eventId, SeatCreateRequest req) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));
        Seat seat = new Seat();
        seat.setEvent(event);
        seat.setSeatNumber(req.seatNumber());
        seat.setPrice(req.price());
        Seat saved = seatRepository.save(seat);
        return toResponse(saved);
    }

    public List<SeatResponse> getSeatsForEvent(Long eventId) {
        return seatRepository.findByEventId(eventId).stream().map(this::toResponse).toList();
    }

    private SeatResponse toResponse(Seat s) {
        return new SeatResponse(s.getId(), s.getSeatNumber(), s.getPrice(), s.isBooked());
    }
}
