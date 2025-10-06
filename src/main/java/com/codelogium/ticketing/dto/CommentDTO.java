package com.codelogium.ticketing.dto;

import com.codelogium.ticketing.dto.UserDTO;

public class CommentDTO {
    private Long id;
    private String content;
    private String createdAt; // Use String or Instant for date/time
    private UserDTO creator; // <-- ADDED: The author of the comment

    // Standard constructor, getters, and setters

    public CommentDTO() {
    }

    public CommentDTO(Long id, String content, String createdAt, UserDTO creator) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.creator = creator;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO getCreator() {
        return creator;
    }

    public void setCreator(UserDTO creator) {
        this.creator = creator;
    }
}
