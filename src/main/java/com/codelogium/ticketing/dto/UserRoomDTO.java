package com.codelogium.ticketing.dto;

public class UserRoomDTO {
    private Long id;
    private RoomDTO room; // Use the simplified RoomDto

    // Standard constructor, getters, and setters

    public UserRoomDTO(Long id, RoomDTO room) {
        this.id = id;
        this.room = room;
    }

    public UserRoomDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoomDTO getRoom() {
        return room;
    }

    public void setRoom(RoomDTO room) {
        this.room = room;
    }
}