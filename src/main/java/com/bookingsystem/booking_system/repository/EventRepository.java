package com.bookingsystem.booking_system.repository;

import com.bookingsystem.booking_system.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = """
        SELECT e.name AS eventName, COUNT(b.id) AS totalBookings
        FROM events e
        LEFT JOIN seats s ON s.event_id = e.id
        LEFT JOIN bookings b ON b.seat_id = s.id AND b.status = 'CONFIRMED'
        GROUP BY e.id, e.name
        ORDER BY totalBookings DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findMostBookedEvents();

    @Query(value = """
        SELECT e.name AS eventName,
               COUNT(s.id) AS totalSeats,
               COUNT(CASE WHEN s.booked = true THEN 1 END) AS bookedSeats,
               ROUND(100.0 * COUNT(CASE WHEN s.booked = true THEN 1 END) / NULLIF(COUNT(s.id), 0), 2) AS occupancyPercent
        FROM events e
        LEFT JOIN seats s ON s.event_id = e.id
        WHERE e.id = :eventId
        GROUP BY e.id, e.name
        """, nativeQuery = true)
    List<Object[]> findOccupancyStats(@Param("eventId") Long eventId);
}


