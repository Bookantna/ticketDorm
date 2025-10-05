package com.codelogium.ticketing.repository;

import com.codelogium.ticketing.entity.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing UserRoom entities, which represents
 * the join table for the Many-to-Many relationship between User and Room.
 */
@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
    // Basic CRUD operations are inherited from JpaRepository
}
