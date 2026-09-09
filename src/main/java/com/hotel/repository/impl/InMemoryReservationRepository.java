package com.hotel.repository.impl;

import com.hotel.model.Reservation;
import com.hotel.repository.ReservationRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;



public class InMemoryReservationRepository implements ReservationRepository{

        private final Map<UUID,Reservation> reservationStorage= new LinkedHashMap<>();

        @Override
        public void save(Reservation reservation){
            reservationStorage.put(reservation.getId(),reservation);
        }

        @Override
        public Optional<Reservation> findById(UUID id){
            return Optional.ofNullable(reservationStorage.get(id));
        }


        @Override
        public List<Reservation> findAll(){
            return new ArrayList(reservationStorage.values());
        }

        @Override
        public List<Reservation> findByUserId(UUID userId){
            return reservationStorage.values().stream()
                    .filter(r-> userId.equals(r.getUserId()))
                    .toList();
        }



        @Override
        public Optional<Reservation> findByCode(String code){
            return reservationStorage.values().stream()
                    .filter(r-> r.getReservationCode().equals(code))
                    .findFirst();//we use find first to return a optional
        }




        @Override
        public List<Reservation> findByRoomNumber(String roomNumber){
            return reservationStorage.values().stream()
                    .filter(r->r.getRoomNumber().equals(roomNumber))
                    .toList();
        }







}