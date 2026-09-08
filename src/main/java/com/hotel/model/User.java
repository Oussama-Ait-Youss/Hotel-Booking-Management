package com.hotel.model;
import java.util.UUID;

public class User {
    private UUID UUID;
    private String fullName;
    private String email;
    private String phone;
    private String password;


    // create the constructor
    public User(UUID UUID,String fullName,String email,String phone,String password){
        this.UUID = UUID;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    //create the getter
    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public UUID getUUID() {
        return UUID;
    }

    //create the setter

    public void setUUID(UUID UUID) {
        this.UUID = UUID;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}