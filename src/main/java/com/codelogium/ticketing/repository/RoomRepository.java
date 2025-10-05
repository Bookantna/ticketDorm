package com.codelogium.ticketing.repository;

import com.codelogium.ticketing.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Finds a Room by its unique invite code. Used during user registration.
     */
    Optional<Room> findByInviteCode(String inviteCode);
}
