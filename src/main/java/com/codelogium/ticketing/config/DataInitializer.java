package com.codelogium.ticketing.config;

import com.codelogium.ticketing.entity.Room;
import com.codelogium.ticketing.entity.RoomDetails;
import com.codelogium.ticketing.repository.RoomDetailsRepository;
import com.codelogium.ticketing.repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoomRepository roomRepository;
    private final RoomDetailsRepository roomDetailsRepository;

    public DataInitializer(RoomRepository roomRepository, RoomDetailsRepository roomDetailsRepository) {
        this.roomRepository = roomRepository;
        this.roomDetailsRepository = roomDetailsRepository;
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