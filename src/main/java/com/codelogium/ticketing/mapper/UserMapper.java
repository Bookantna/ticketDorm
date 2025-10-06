package com.codelogium.ticketing.mapper;

import com.codelogium.ticketing.dto.RoomDTO;
import com.codelogium.ticketing.dto.UserDTO;
import com.codelogium.ticketing.dto.UserRoomDTO;
import com.codelogium.ticketing.entity.Room;
import com.codelogium.ticketing.entity.User;
import com.codelogium.ticketing.entity.UserRoom;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

// UserMapper.java
@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        // Map other simple fields...

        // 1. Map the UserRoom collection
        List<UserRoomDTO> membershipDtos = user.getMemberships().stream()
                .map(this::toUserRoomDto)
                .collect(Collectors.toList());
        dto.setMemberships(membershipDtos);

        // 2. Map tickets and comments similarly...

        return dto;
    }

    private UserRoomDTO toUserRoomDto(UserRoom userRoom) {
        UserRoomDTO dto = new UserRoomDTO();
        dto.setId(userRoom.getId());
        // Map the Room relationship, making sure to use RoomDto
        dto.setRoom(toRoomDto(userRoom.getRoom()));
        return dto;
    }

    private RoomDTO toRoomDto(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setName(room.getRoomNumber());
        // Do NOT map room.getMemberships()
        return dto;
    }
}