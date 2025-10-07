package com.codelogium.ticketing.config;

import com.codelogium.ticketing.entity.Room;
import com.codelogium.ticketing.entity.RoomDetails;
import com.codelogium.ticketing.entity.User;
import com.codelogium.ticketing.entity.UserRoom;
import com.codelogium.ticketing.entity.enums.UserRole;
import com.codelogium.ticketing.repository.RoomDetailsRepository;
import com.codelogium.ticketing.repository.RoomRepository;
import com.codelogium.ticketing.repository.UserRepository;
import com.codelogium.ticketing.repository.UserRoomRepository;
import com.codelogium.ticketing.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.management.relation.Role;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoomRepository roomRepository;
    private final RoomDetailsRepository roomDetailsRepository;
    private final UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRoomRepository userRoomRepository;
    private final UserService userService;

    public DataInitializer(RoomRepository roomRepository, RoomDetailsRepository roomDetailsRepository, UserRepository userRepository, UserRoomRepository userRoomRepository, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.roomRepository = roomRepository;
        this.roomDetailsRepository = roomDetailsRepository;
        this.userRepository = userRepository;
        this.userRoomRepository = userRoomRepository;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Executes immediately after dependency injection is complete.
     */
    @PostConstruct
    public void init() {
        System.out.println("Initializing Room data in H2 database...");



        // **(Optional but recommended for idempotent initialization)**
        // Check if data already exists to prevent repeated creation on restart
        if (roomRepository.count() > 0) {
            System.out.println("Data already present. Skipping initialization.");
            return;
        }

        // Create and save the first room
        Room adminRoom = new Room("Staff");
        adminRoom = roomRepository.save(adminRoom);

// Create admin


        User admin = new User("admin","admin","admin@admin.com", UserRole.STAFF);
        admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));
        admin = userRepository.save(admin); // save first
        userRepository.save(admin);

// Now we can create UserRoom
        UserRoom adminRoomAssign = new UserRoom(admin, adminRoom);
        adminRoomAssign = userRoomRepository.save(adminRoomAssign); // safe to save

// Optionally set back to admin
        Set<UserRoom> memberships = new HashSet<>();
        memberships.add(adminRoomAssign);
        admin.setMemberships(memberships);
        userRepository.save(admin);// optional, to update memberships



        for(int i=1;i<11;i++){
            Room room = new Room(String.valueOf(100+i));
            // Save the room first to get the auto-generated ID
            room = roomRepository.save(room);

            // ----------------------------------------------------------------------
            // **THE CRITICAL FIX:**
            // Use a constructor or setters that DO NOT set the ID for RoomDetails.
            // By NOT passing room.getId() here, Hibernate treats it as a NEW entity.
            // Assuming this is your new constructor:
            // RoomDetails(Room room, String floor, String manager, Instant createdTime)
            // ----------------------------------------------------------------------
            RoomDetails roomDetails = new RoomDetails(
                    room,
                    "Floor 1",
                    "John",
                    Instant.now().minusSeconds((long)(i * 3600 * 24 * 7))
            );

            roomDetailsRepository.save(roomDetails);
        }

        System.out.println("Successfully added rooms to the database.");
    }
}