package com.hotel.repository;

import com.hotel.model.Room;
import java.util.List;
import java.util.Optional;



public interface RoomRepository {
    public void save(Room room);
    Optional<Room> findByRoomNumber(String roomNumber);
    public List<Room> findAll();
}