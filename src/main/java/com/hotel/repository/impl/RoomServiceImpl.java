package com.hotel.service.impl;

import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.repository.ReservationRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.service.RoomService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public RoomServiceImpl(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || checkIn.isBefore(LocalDate.now()) || !checkOut.isAfter(checkIn)) {
            return List.of();
        }

        return roomRepository.findAll().stream()
                .filter(room -> isRoomAvailable(room.getRoomNumber(), checkIn, checkOut))
                .toList();
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, int guests) {
        if (guests <= 0) {
            return List.of();
        }

        // Réutilise la disponibilité sur les dates, puis filtre par capacité
        return getAvailableRooms(checkIn, checkOut).stream()
                .filter(room -> room.getCapacity() >= guests)
                .toList();
    }

    @Override
    public boolean isRoomAvailable(String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        if (roomNumber == null || checkIn == null || checkOut == null) {
            return false;
        }

        List<Reservation> reservations = reservationRepository.findByRoomNumber(roomNumber);

        // Une chambre est libre si AUCUNE réservation active ne chevauche la période
        return reservations.stream().noneMatch(res -> res.overlapsWith(checkIn, checkOut));
    }

    @Override
    public Optional<Room> getRoomByNumber(String roomNumber) {
        if (roomNumber == null) {
            return Optional.empty();
        }
        return roomRepository.findByRoomNumber(roomNumber);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}