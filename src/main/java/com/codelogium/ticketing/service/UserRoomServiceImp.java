package com.codelogium.ticketing.service;

import com.codelogium.ticketing.entity.Room;
import com.codelogium.ticketing.entity.UserRoom;
import com.codelogium.ticketing.exception.ResourceNotFoundException;
import com.codelogium.ticketing.repository.UserRoomRepository;
import org.springframework.stereotype.Service; // ADDED: Required for Spring bean registration

import java.util.List;
import java.util.Optional;

// NOTE: import com.codelogium.ticketing.entity.User is unused and has been removed.

@Service // Marks this class as a Spring Service bean
public class UserRoomServiceImp implements UserRoomService{

    private final UserRoomRepository userRoomRepository; // Made final for best practice

    // Constructor injection is the recommended way
    public UserRoomServiceImp(UserRoomRepository userRoomRepository) {
        this.userRoomRepository = userRoomRepository;
    }

    @Override
    public List<Room> retrieveRooms(Long userid) {
        // FIX: Assuming userRoomRepository.getRoomsByUserid(userid) returns List<Room>.
        // Removed the incorrect call to unwrapUserRoom which was designed for a single entity,
        // as retrieving a list should return an empty list if no rooms are found, not throw an exception.
        return userRoomRepository.findRoomsByUserId(userid);
    }

}
