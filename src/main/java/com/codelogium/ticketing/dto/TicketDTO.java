package com.codelogium.ticketing.dto;

import com.codelogium.ticketing.entity.enums.Status;
import com.codelogium.ticketing.entity.enums.Category;
import com.codelogium.ticketing.entity.enums.Priority;

import java.time.Instant;
import java.util.List;

public class TicketDTO {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private Instant creationDate;
    private Status status;
    private Category category;
    private Priority priority;
    private UserDTO creator;
    private List<CommentDTO> comments;
    private List<TicketRoomDTO> roomAssociations; // <-- ADDED: The list of associated rooms

    // Default Constructor
    public TicketDTO() {
    }

    // Full Constructor (simplified for example)
    public TicketDTO(Long id, String title, String description, String imageUrl, Instant creationDate, Status status, Category category, Priority priority, UserDTO creator, List<CommentDTO> comments, List<TicketRoomDTO> roomAssociations) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.creationDate = creationDate;
        this.status = status;
        this.category = category;
        this.priority = priority;
        this.creator = creator;
        this.comments = comments;
        this.roomAssociations = roomAssociations;
    }

    // --- GETTERS AND SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public UserDTO getCreator() {
        return creator;
    }

    public void setCreator(UserDTO creator) {
        this.creator = creator;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    // New Getter
    public List<TicketRoomDTO> getRoomAssociations() {
        return roomAssociations;
    }

    // New Setter, required by the TicketMapper
    public void setRoomAssociations(List<TicketRoomDTO> roomAssociations) {
        this.roomAssociations = roomAssociations;
    }
}
