package com.codelogium.ticketing.dto;

import com.codelogium.ticketing.entity.User;
import com.codelogium.ticketing.entity.enums.UserRole;
import java.util.List;
import com.codelogium.ticketing.dto.*;


public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String password;
    private UserRole role;

    // Replaced JPA entity 'Ticket' with the simplified 'TicketDto'
    private List<TicketDTO> tickets;

    // Replaced JPA entity 'Comment' with the simplified 'CommentDto'
    private List<CommentDTO> comments;

    // Replaced JPA entity 'UserRoom' with the simplified 'UserRoomDto'
    private List<UserRoomDTO> memberships;


    // --- CONSTRUCTORS ---

    public UserDTO() {}

    public UserDTO(Long id, String username, String email, UserRole role,
                   List<TicketDTO> tickets, List<CommentDTO> comments,
                   List<UserRoomDTO> memberships) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.tickets = tickets;
        this.comments = comments;
        this.memberships = memberships;
    }


    public User toEntity() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setEmail(this.email);

        // Ensure a default role if not provided in the form (e.g., RENTER)
        user.setRole(UserRole.RENTER);


        return user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<TicketDTO> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketDTO> tickets) {
        this.tickets = tickets;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public List<UserRoomDTO> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<UserRoomDTO> memberships) {
        this.memberships = memberships;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}