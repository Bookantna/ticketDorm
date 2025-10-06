package com.codelogium.ticketing.repository;

import com.codelogium.ticketing.entity.RoomDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomDetailsRepository extends JpaRepository<RoomDetails, Long> {
}
