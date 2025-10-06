package com.codelogium.ticketing.dto;

public class TicketRoomDTO {
    private Long id;
    private RoomDTO room;

    public TicketRoomDTO() {}

    public TicketRoomDTO(Long id, RoomDTO room) {
        this.id = id;
        this.room = room;
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
