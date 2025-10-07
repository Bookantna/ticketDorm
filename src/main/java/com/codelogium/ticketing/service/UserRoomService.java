package com.codelogium.ticketing.service;

import com.codelogium.ticketing.entity.Room;
import com.codelogium.ticketing.entity.User;

import java.util.List;

public interface UserRoomService {
    List<Room> retrieveRooms(Long userid);
}
