package com.codelogium.ticketing.entity;

import jakarta.persistence.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- User Association (One-to-Many to Join Table) ---
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRoom> memberships = new HashSet<>();
    // --------------------------------------

    @Column(unique = true, nullable = false)
    private String roomNumber;

    @Column(unique = true)
    private String inviteCode;

    // --- CONSTRUCTORS ---

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TicketRoom> ticketAssociations = new HashSet<>();

    @OneToOne(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private RoomDetails roomDetails;

    // REQUIRED by JPA/Hibernate: Public no-argument constructor
    public Room() {
        this.memberships = new HashSet<>();
    }

    // Convenience constructor for creating a room instance with a number
    public Room(String roomNumber) {
        this();
        this.roomNumber = roomNumber;
        this.inviteCode = generateInviteCode(roomNumber);
    }

    public Room(Long id, Set<UserRoom> memberships, String roomNumber, String inviteCode, Set<TicketRoom> ticketAssociations, RoomDetails roomDetails) {
        this.id = id;
        this.memberships = memberships;
        this.roomNumber = roomNumber;
        this.inviteCode = inviteCode;
        this.ticketAssociations = ticketAssociations;
        this.roomDetails = roomDetails;
    }

    // --- JPA LIFECYCLE CALLBACKS ---

    @PrePersist
    protected void onCreate() {
        if (this.inviteCode == null && this.roomNumber != null) {
            this.inviteCode = generateInviteCode(this.roomNumber);
        }
    }

    // --- BUSINESS LOGIC ---
    private String generateInviteCode(String roomNumber) {
        if (roomNumber == null || roomNumber.isEmpty()) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(roomNumber.getBytes(StandardCharsets.UTF_8));
            String fullHash = bytesToHex(encodedhash);
            return fullHash.substring(0, Math.min(fullHash.length(), 8)).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found.", e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // --- GETTERS AND SETTERS ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<UserRoom> getMemberships() {
        return memberships;
    }

    public void setMemberships(Set<UserRoom> memberships) {
        this.memberships = memberships;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    // Setter that regenerates the inviteCode if the roomNumber changes
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
        if (roomNumber != null) {
            this.inviteCode = generateInviteCode(roomNumber);
        }
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Set<TicketRoom> getTicketAssociations() {
        return ticketAssociations;
    }

    public void setTicketAssociations(Set<TicketRoom> ticketAssociations) {
        this.ticketAssociations = ticketAssociations;
    }

    public RoomDetails getRoomDetails() {
        return roomDetails;
    }

    public void setRoomDetails(RoomDetails roomDetails) {
        this.roomDetails = roomDetails;
    }
}