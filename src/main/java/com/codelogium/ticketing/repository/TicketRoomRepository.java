package com.codelogium.ticketing.repository;

import com.codelogium.ticketing.entity.TicketRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRoomRepository extends JpaRepository<TicketRoom, Long> {
    // Custom query methods for fetching/searching TicketRoom entities can be added here.
}
