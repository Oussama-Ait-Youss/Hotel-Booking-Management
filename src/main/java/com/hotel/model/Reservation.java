package com.hotel.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


public class Reservation {
    private UUID id;
    private String reservationCode;
    private UUID userId;
    private String roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int numberOfGuests;
    private int numberOfNights;
    private BigDecimal totalPrice;
    private ReservationStatus status;
    private LocalDate createdAt;

    //create the constructor
    public Reservation(UUID id,String reservationCode,UUID userId,String roomNumber,LocalDate checkIn,LocalDate checkOut,int numberOfGuests,int numberOfNights,BigDecimal totalPrice,ReservationStatus status,LocalDate createdAt){
        this.id = id;
        this.reservationCode = reservationCode;
        this.userId = userId;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfGuests = numberOfGuests;
        this.numberOfNights = numberOfNights;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    //create the getters


    public UUID getId() {
        return id;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public boolean overlapsWith(LocalDate requestedCheckIn, LocalDate requestedCheckOut) {
        return requestedCheckIn != null
                && requestedCheckOut != null
                && requestedCheckIn.isBefore(checkOut)
                && requestedCheckOut.isAfter(checkIn);
    }

    //create the setters

    public void setId(UUID id) {
        this.id = id;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public void setNumberOfNights(int numberOfNights) {
        this.numberOfNights = numberOfNights;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}