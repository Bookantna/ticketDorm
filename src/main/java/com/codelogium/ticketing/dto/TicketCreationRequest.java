package com.codelogium.ticketing.dto;

import com.codelogium.ticketing.entity.enums.Category;
import com.codelogium.ticketing.entity.enums.Priority;
import com.codelogium.ticketing.entity.enums.Status;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class TicketCreationRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String imageUrl;

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotNull(message = "Status is required")
    private Status status;

    // Renamed field to reflect that it holds string-based room numbers, not numeric IDs
    private List<String> roomNumbers;

    // --- CONSTRUCTORS ---
    // Default constructor is required by Jackson
    public TicketCreationRequest() {}

    // Parameterized constructor explicitly marked with @JsonCreator for reliable deserialization
    @JsonCreator
    public TicketCreationRequest(
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("imageUrl") String imageUrl,
            @JsonProperty("category") Category category,
            @JsonProperty("priority") Priority priority,
            @JsonProperty("status") Status status,
            @JsonProperty("roomNumbers") List<String> roomNumbers) { // Updated parameter and @JsonProperty
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.roomNumbers = roomNumbers;
    }

    // --- GETTERS AND SETTERS ---
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public List<String> getRoomNumbers() { return roomNumbers; } // Updated method name
    public void setRoomNumbers(List<String> roomNumbers) { this.roomNumbers = roomNumbers; } // Updated method name

    // NOTE: CreationDate and Creator are NOT included here as they are set by the server.
}
