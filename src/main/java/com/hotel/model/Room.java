package com.hotel.model;


public class Room {
    private int roomNumber;
    private RoomType type;
    private int capacity;
    private BigDecimal pricePerNight;
    private RoomStatus status;



    //create the constructor
    public Room(int roomNumber,RoomType type,int capacity,BigDecimal pricePerNight,RoomStatus status){
        this.roomNumber = roomNumber;
        this.type = type;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
        this.status = status;
    }


    //create the getters

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public RoomStatus getStatus() {
        return status;
    }

    //create the setter

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}