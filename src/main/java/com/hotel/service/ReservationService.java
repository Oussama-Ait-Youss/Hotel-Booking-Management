package com.hotel.service;

import com.hotel.model.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationService {

    Reservation createReservation(UUID userId, String roomNumber, LocalDate checkIn, LocalDate checkOut);

    boolean cancelReservation(UUID reservationId);

    Optional<Reservation> getReservationById(UUID id);

    Optional<Reservation> getReservationByCode(String code);

    List<Reservation> getReservationsByUserId(UUID userId);

    List<Reservation> getAllReservations();
}