package com.bookingsystem.booking_system.service;

import com.bookingsystem.booking_system.dto.EventCreateRequest;
import com.bookingsystem.booking_system.dto.EventResponse;
import com.bookingsystem.booking_system.entity.Event;
import com.bookingsystem.booking_system.exception.ResourceNotFoundException;
import com.bookingsystem.booking_system.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventResponse createEvent(EventCreateRequest req) {
        Event event = new Event();
        event.setName(req.name());
        event.setDescription(req.description());
        event.setEventTime(req.eventTime());
        event.setVenue(req.venue());
        Event saved = eventRepository.save(event);
        return toResponse(saved);
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream().map(this::toResponse).toList();
    }

    public EventResponse getEvent(Long id) throws Exception {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
        return toResponse(event);
    }

    private EventResponse toResponse(Event e) {
        return new EventResponse(e.getId(), e.getName(), e.getDescription(), e.getEventTime(), e.getVenue());
    }
}

