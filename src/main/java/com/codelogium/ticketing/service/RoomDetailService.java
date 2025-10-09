package com.codelogium.ticketing.service;

import com.codelogium.ticketing.entity.RoomDetails;

public interface RoomDetailService {
    RoomDetails retrieveRoomDetailByRoomId(Long roomId);
}
