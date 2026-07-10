package com.bookingsystem.booking_system.dto;

public record AuthResponse(String token, String name, String email, String role) {}
