package com.bookingsystem.booking_system.repository;

import com.bookingsystem.booking_system.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = """
    SELECT e.name AS eventName, SUM(s.price) AS totalRevenue
    FROM bookings b
    JOIN seats s ON s.id = b.seat_id
    JOIN events e ON e.id = s.event_id
    WHERE b.status = 'CONFIRMED'
    GROUP BY e.id, e.name
    ORDER BY totalRevenue DESC
    """, nativeQuery = true)
    List<Object[]> findRevenueByEvent();


}