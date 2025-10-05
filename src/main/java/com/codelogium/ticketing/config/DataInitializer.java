package com.codelogium.ticketing.config;

import com.codelogium.ticketing.entity.Room;
import com.codelogium.ticketing.repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    // Assuming you have a RoomRepository interface defined
    private final RoomRepository roomRepository;

    public DataInitializer(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * Executes immediately after dependency injection is complete.
     */
    @PostConstruct
    public void init() {
        // Log the action (optional, but good practice)
        System.out.println("Initializing Room data in H2 database...");

        // Create and save the first room
        Room staff = new Room("Staff");
        roomRepository.save(staff);
        
        Room room1 = new Room("101"); // Assuming your custom constructor takes roomNumber
        roomRepository.save(room1);

        // Create and save a second room
        Room room2 = new Room("20A");
        roomRepository.save(room2);

        System.out.println("Successfully added 2 rooms to the database.");
    }
}