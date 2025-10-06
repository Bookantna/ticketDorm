package com.codelogium.ticketing.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// You can add fields like 'areaAffectedDetails' or 'reportedTimeInRoom' here if needed.

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ticket_rooms")
public class TicketRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many TicketRooms belong to one Ticket
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    // Many TicketRooms belong to one Room
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // Add constructors, getters, and setters (Lombok's @Data handles this)


    public TicketRoom(Long id, Ticket ticket, Room room) {
        this.id = id;
        this.ticket = ticket;
        this.room = room;
    }

    public TicketRoom() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}