package com.codelogium.ticketing.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codelogium.ticketing.entity.User;
import com.codelogium.ticketing.entity.Room;
import com.codelogium.ticketing.entity.UserRoom; // Import new join entity
import com.codelogium.ticketing.exception.ResourceNotFoundException;
import com.codelogium.ticketing.repository.UserRepository;
import com.codelogium.ticketing.repository.RoomRepository;
import com.codelogium.ticketing.repository.UserRoomRepository; // Import new repository

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImp implements UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private RoomRepository roomRepository;
    private UserRoomRepository userRoomRepository; // NEW DEPENDENCY for M-to-M

    // Manual constructor for explicit dependency injection control
    public UserServiceImp(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, RoomRepository roomRepository, UserRoomRepository userRoomRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roomRepository = roomRepository;
        this.userRoomRepository = userRoomRepository; // Initialize new dependency
    }

    @Override
    @Transactional
    public User createUser(User user, String inviteCode) {

        // 1. Encoding the password before saving
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        // 2. Look up the room using the invite code
        Room room = roomRepository.findByInviteCode(inviteCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException(0L, Room.class)); // Throws if code is invalid

        // 3. Save the new User entity to the database first (to get the generated ID)
        User registeredUser = userRepository.save(user);

        // 4. Create the Many-to-Many join entity (UserRoom)
        UserRoom userRoomMembership = new UserRoom(registeredUser, room);

        // 5. Save the membership to link the User and the Room
        userRoomRepository.save(userRoomMembership);

        // Return the fully saved and linked user
        return registeredUser;
    }

    @Override
    public User retrieveUser(Long userId) {
        return unwrapUser(userId, userRepository.findById(userId));
    }

    @Override
    public User retrieveUser(String username) {
        return unwrapUser(404L, userRepository.findByUsername(username));
    }

    @Override
    public void removeUser(Long userId) {
        User retrievedUser = unwrapUser(userId, userRepository.findById(userId));

        userRepository.delete(retrievedUser);
    }

    public void validateUserExists(Long userId) {
        if(!userRepository.existsById(userId)) throw new ResourceNotFoundException(userId, User.class);
    }

    public static User unwrapUser(Long userId, Optional<User> optionalUser) {
        return optionalUser.orElseThrow(() -> new ResourceNotFoundException(userId, User.class));
    }
}
