//package com.hotel.service;
//
//
//
//
//public class RoomService {
//
//    private final RoomRepository roomRepository;
//
//    public RoomService(RoomRepository roomRepository){
//        this.roomRepository = roomRepository;
//    }
//
//
//
//
//    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut){
//        if (checkIn == null || checkOut == null || checkIn.isBefore(LocalDate.now()) || !checkOut.isAfter(checkIn)) {
//            return List.of();
//        }else{
//            return roomRepository.findAll().stream()
//                    .filter(r-> r.getStatus().equals("AVAILABLE"))
//                    .toList();
//        }
//    }
//
//    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, int guests){
//        if (checkIn == null || checkOut == null || checkIn.isBefore(LocalDate.now()) || !checkOut.isAfter(checkIn)) {
//            return List.of();
//        }
//        return roomRepository.findAll().stream()
//                    .filter(r-> r.getCapacity()>=guests)
//                .toList();
//    }
//
//
//
//
//
//
//}
package com.hotel.service;

import com.hotel.model.Room;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut);
    List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, int guests);
    boolean isRoomAvailable(String roomNumber, LocalDate checkIn, LocalDate checkOut);
    Optional<Room> getRoomByNumber(String roomNumber);
    List<Room> getAllRooms();
}