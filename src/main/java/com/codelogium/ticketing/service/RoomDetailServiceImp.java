package com.codelogium.ticketing.service;

import com.codelogium.ticketing.entity.RoomDetails;
import com.codelogium.ticketing.entity.Ticket;
import com.codelogium.ticketing.repository.RoomDetailsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomDetailServiceImp implements RoomDetailService {

    private final RoomDetailsRepository roomDetailsRepository;

    public RoomDetailServiceImp(RoomDetailsRepository roomDetailsRepository) {
        this.roomDetailsRepository = roomDetailsRepository;
    }

    public RoomDetails retrieveRoomDetailByRoomId(Long roomId){

        RoomDetails roomDetails = roomDetailsRepository.findByRoomId(roomId);

        return roomDetails;
    }
}
