package com.codelogium.ticketing.dto;

public class RoomDTO {
    private Long id;
    private String roomNumber;

    // Standard constructor, getters, and setters


    public RoomDTO(Long id, String roomNumber) {
        this.id = id;
        this.roomNumber = roomNumber;
    }

    public RoomDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}