package com.bookingsystem.booking_system.repository;

import com.bookingsystem.booking_system.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {}
