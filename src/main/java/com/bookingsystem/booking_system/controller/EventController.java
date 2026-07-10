package com.bookingsystem.booking_system.controller;

import com.bookingsystem.booking_system.dto.EventCreateRequest;
import com.bookingsystem.booking_system.dto.EventResponse;
import com.bookingsystem.booking_system.service.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventCreateRequest req) {
        return ResponseEntity.ok(eventService.createEvent(req));
    }

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(eventService.getAllEvents(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getOne(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(eventService.getEvent(id));
    }
}

