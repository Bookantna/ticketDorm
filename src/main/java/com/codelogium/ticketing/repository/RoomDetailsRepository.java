package com.codelogium.ticketing.repository;

import com.codelogium.ticketing.entity.RoomDetails;
import com.codelogium.ticketing.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomDetailsRepository extends JpaRepository<RoomDetails, Long> {

    @Query("SELECT r FROM RoomDetails r WHERE (r.id = :roomId)")
    RoomDetails findByRoomId(Long roomId);
}
