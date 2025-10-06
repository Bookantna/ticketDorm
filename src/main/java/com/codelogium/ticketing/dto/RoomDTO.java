package com.codelogium.ticketing.dto;

public class RoomDTO {
    private Long id;
    private String name;

    // Standard constructor, getters, and setters

    public RoomDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoomDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}