package com.codelogium.ticketing.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.codelogium.ticketing.entity.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Added unique = true to enforce unique usernames
    @NotBlank(message = "Username cannot be null or blank")
    @Size(min = 5, max = 16, message = "Username must be between 5 and 8 characters")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Password cannot be null or blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;


    @NotBlank(message = "Email cannot be null or blank")
    @Column(nullable = false)
    @Email(message = "Invalid email format")
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // --- Many-to-Many Association via UserRoom Join Entity ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<UserRoom> memberships = new HashSet<>();

    // --- CONSTRUCTORS ---

    // 1. REQUIRED by JPA/Hibernate: Public no-argument constructor
    public User() {
        // Initialize collections to prevent NullPointerExceptions
        this.tickets = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.memberships = new HashSet<>();
    }

    // 2. All-arguments constructor used for convenient creation
    public User(Long id, String username, String password, String email, UserRole role, List<Ticket> tickets, List<Comment> comments, Set<UserRoom> memberships) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.tickets = tickets;
        this.comments = comments;
        this.memberships = memberships;
    }

    // 3. Custom constructor for creating new users before ID/collections are set
    public User(String username, String password, String email, UserRole role) {
        this(); // Calls the default constructor to initialize collections
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }


    // --- GETTERS AND SETTERS ---

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Set<UserRoom> getMemberships() {
        return memberships;
    }

    public void setMemberships(Set<UserRoom> memberships) {
        this.memberships = memberships;
    }
}
