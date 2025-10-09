package com.codelogium.ticketing.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

/**
 * Stores complex, optional metadata about a Room (e.g., floor plan, asset lists, primary contacts).
 * This entity has a one-to-one relationship with Room, sharing the same primary key (Room ID).
 */
@Entity
@Table(name = "room_details")
@NoArgsConstructor // Required by JPA
public class RoomDetails {

    // The primary key of this entity is also the foreign key to the Room entity.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Use @MapsId to specify that the PK is mapped from the associated entity.
    @OneToOne
    @JoinColumn(name = "room_id")
    private Room room;

    // This field will hold descriptive floor information or the large SVG data.
    @Column(columnDefinition = "TEXT")
    private String floor;

    private String desc;

    public RoomDetails() {
    }

    public RoomDetails(Room room, String floor, String desc, Instant lastInspectionDate) {
        this.room = room;
        this.floor = floor;
        this.desc = desc;
        this.lastInspectionDate = lastInspectionDate;
    }

    private Instant lastInspectionDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Instant getLastInspectionDate() {
        return lastInspectionDate;
    }

    public void setLastInspectionDate(Instant lastInspectionDate) {
        this.lastInspectionDate = lastInspectionDate;
    }
}