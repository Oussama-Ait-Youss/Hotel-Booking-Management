package com.hotel.repository;
import com.hotel.model.Reservation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;




public interface ReservationRepository {

    public void save(Reservation reservation);
    Optional<Reservation> findById(UUID uuid);
    public List<Reservation> findAll();
    public List<Reservation> findByUserId(UUID userId);
    Optional<Reservation> findByCode(String code);
    public List<Reservation> findByRoomNumber(String roomNumber);

}