package com.hotel.repository.impl;

import com.hotel.repository.RoomRepository;
import com.hotel.model.Room;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//Il manque les imports de Map, LinkedHashMap, List, ArrayList et Optional du package java.util.



public class InMemoryRoomRepository implements RoomRepository{

    private final Map<String,Room> storage= new LinkedHashMap<>();

    @Override
    public void save(Room room){
        storage.put(room.getRoomNumber(),room);
    }

    @Override
    public Optional<Room> findByRoomNumber(String roomNumber) {
        if (roomNumber == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(roomNumber.trim()));
    }

    @Override
    public List<Room> findAll(){
        return new ArrayList(storage.values());
    }




}