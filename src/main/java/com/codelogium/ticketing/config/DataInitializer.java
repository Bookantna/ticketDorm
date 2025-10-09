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
import java.util.List;
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

        Room technicianRoom = new Room("Technician Room");
        technicianRoom = roomRepository.save(technicianRoom);

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


        User technician = new User("technician","technician","technician@admin.com", UserRole.TECHNICIAN);
        technician.setPassword(bCryptPasswordEncoder.encode(technician.getPassword()));
        technician = userRepository.save(technician); // save first
        userRepository.save(technician);

// Now we can create UserRoom
        UserRoom technicianRoomAssign = new UserRoom(technician, technicianRoom);
        technicianRoomAssign = userRoomRepository.save(technicianRoomAssign); // safe to save

// Optionally set back to admin
        Set<UserRoom> memberships2 = new HashSet<>();
        memberships2.add(technicianRoomAssign);
        technician.setMemberships(memberships2);
        userRepository.save(technician);// optional, to update memberships


        List<String> roomDescriptions = List.of(
                "1 Bedroom, 1 Bathroom",
                "2 Bedrooms, 2 Bathrooms",
                "1 Bedroom, 2 Bathrooms",
                "2 Bedrooms, 1 Bathroom",
                "Studio Room with 1 Bathroom",
                "3 Bedrooms, 2 Bathrooms",
                "1 Bedroom, Shared Bathroom",
                "2 Bedrooms, Shared Bathroom",
                "Deluxe Suite with 2 Bedrooms and 2 Bathrooms",
                "Family Suite with 3 Bedrooms and 3 Bathrooms"
        );

        for (int i = 0; i < 10; i++) {
            Room room = new Room(String.valueOf(101 + i));
            room = roomRepository.save(room);

            // Create room details
            RoomDetails roomDetails = new RoomDetails(
                    room,
                    "Floor 1",
                    roomDescriptions.get(i),
                    Instant.now().minusSeconds((long) ((i + 1) * 3600 * 24 * 7))
            );

            if(i>5){
                roomDetails.setFloor("Floor 2");
            }
            roomDetailsRepository.save(roomDetails);
        }


        System.out.println("Successfully added rooms to the database.");
    }
}