package com.codelogium.ticketing.repository;

import com.codelogium.ticketing.entity.Room;
import com.codelogium.ticketing.entity.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing UserRoom entities, which represents
 * the join table for the Many-to-Many relationship between User and Room.
 */
@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
    @Query("SELECT ur.room FROM UserRoom ur WHERE ur.user.id = :userId")
    List<Room> findRoomsByUserId(@Param("userId") Long userId);
}
