package com.codelogium.ticketing.repository;

import com.codelogium.ticketing.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {


    List<Room> findAllByRoomNumberIn(List<String> roomNumbers);
    Optional<Room> findByInviteCode(String inviteCode);
}
