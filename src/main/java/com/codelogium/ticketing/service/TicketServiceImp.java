package com.codelogium.ticketing.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.codelogium.ticketing.dto.TicketCreationRequest;
import com.codelogium.ticketing.entity.*;
import com.codelogium.ticketing.repository.*;
import org.springframework.stereotype.Service;

import com.codelogium.ticketing.dto.TicketInfoUpdateDTO;
import com.codelogium.ticketing.dto.TicketStatusUpdateDTO;
import com.codelogium.ticketing.entity.enums.Status;
import com.codelogium.ticketing.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;

import static com.codelogium.ticketing.util.EntityUtils.*;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TicketServiceImp implements TicketService {

    private TicketRepository ticketRepository;
    private UserRepository userRepository;
    private AuditLogRepository auditLogRepository;
    private RoomRepository roomRepository;
    private TicketRoomRepository ticketRoomRepository;

    public TicketServiceImp(TicketRepository ticketRepository, UserRepository userRepository, AuditLogRepository auditLogRepository, RoomRepository roomRepository, TicketRoomRepository ticketRoomRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.roomRepository = roomRepository;
        this.ticketRoomRepository = ticketRoomRepository;
    }

    @Override
    @Transactional
    public Ticket createTicket(Long userId, TicketCreationRequest request) {
        // 1. Fetch Creator User Entity
        // NOTE: UserServiceImp.unwrapUser is assumed to handle the Optional and exception.
        User creator = UserServiceImp.unwrapUser(userId, userRepository.findById(userId));

        // 2. Map DTO to Entity and set server-managed fields
        Ticket ticketEntity = new Ticket();
        ticketEntity.setTitle(request.getTitle());
        ticketEntity.setDescription(request.getDescription());
        ticketEntity.setImageUrl(request.getImageUrl());
        ticketEntity.setCategory(request.getCategory());
        ticketEntity.setPriority(request.getPriority());

        // Set server-managed defaults
        ticketEntity.setCreator(creator);
        ticketEntity.setStatus(Status.NEW); // Default status set by server
        ticketEntity.setCreationDate(Instant.now());

        // 3. Save the Ticket Entity to persist it (gets ID before associations)
        Ticket createdTicket = ticketRepository.save(ticketEntity);

        // 4. Handle many-to-many relationship linking rooms
        if (request.getRoomNumbers() != null && !request.getRoomNumbers().isEmpty()) {
            linkTicketToRooms(createdTicket, request.getRoomNumbers());
        }

        // 5. Log ticket creation
        auditLogRepository.save(new AuditLog(
                null,
                createdTicket.getId(),
                null,
                userId,
                "TICKET_CREATED",
                null,
                createdTicket.getStatus().toString(),
                Instant.now()));

        auditLogRepository.flush();

        return createdTicket;
    }

    // Update Ticket by the creator
    @Transactional
    @Override
    public Ticket updateTicketInfo(Long ticketId, Long userId, TicketInfoUpdateDTO dto) {
        // Verify user existS
        validateUser(userId);

        // Get the ticket and verify it belongs to this user
        Ticket retrievedTicket = unwrapTicket(ticketId, ticketRepository.findByIdAndCreatorId(ticketId, userId));

        updateIfNotNull(retrievedTicket::setTitle, dto.getTitle());
        updateIfNotNull(retrievedTicket::setDescription, dto.getDescription());
        updateIfNotNull(retrievedTicket::setCategory, dto.getCategory());
        updateIfNotNull(retrievedTicket::setPriority, dto.getPriority());

        // Save ticket update
        return ticketRepository.save(retrievedTicket);

    }

    @Transactional
    @Override
    public Ticket updateTicketStatus(Long ticketId, Long userId, TicketStatusUpdateDTO dto) {
        // validate user exists
        validateUser(userId);

        // Get the ticket and verify it belongs to this user
        Ticket retrievedTicket = unwrapTicket(ticketId, ticketRepository.findByIdAndCreatorId(ticketId, userId));

        // Store old status before making changes
        Status oldStatus = retrievedTicket.getStatus();

        updateIfNotNull(retrievedTicket::setStatus, dto.getStatus());

        Ticket savedTicket = ticketRepository.save(retrievedTicket);
        // Log status change if only there's an actual modification
        if (isStatusChanged(oldStatus, dto.getStatus())) {
            auditLogRepository.save(new AuditLog(
                    null,
                    ticketId,
                    null,
                    userId,
                    "STATUS_UPDATED",
                    oldStatus.toString(),
                    dto.getStatus().toString(),
                    Instant.now()));

            auditLogRepository.flush(); // Ensure immediate persistence

        }
        return savedTicket;
    }
    private void linkTicketToRooms(Ticket ticket, List<String> roomNumbers) {
        // Find rooms using the list of room numbers
        List<Room> rooms = roomRepository.findAllByRoomNumberIn(roomNumbers);

        if (rooms.size() != roomNumbers.size()) {
            // Throws if one or more room numbers in the request are invalid or not found.
            throw new ResourceNotFoundException("One or more rooms specified were not found or room numbers were invalid.");
        }

        // Create and save TicketRoom associations
        List<TicketRoom> associations = rooms.stream()
                .map(room -> new TicketRoom(null, ticket, room))
                .collect(Collectors.toList());

        // Persist the join entities within the active transaction
        ticketRoomRepository.saveAll(associations);
    }

    @Override
    public Ticket retrieveTicket(Long ticketId, Long userId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));
    }

    @Override
    public List<Ticket> retrieveTicketsByCreator(Long userId) {
        validateUser(userId);

        List<Ticket> tickets = ticketRepository.findByCreatorId(userId);
        if( tickets == null || tickets.size() == 0) throw new ResourceNotFoundException("No tickets created yet.");

        return tickets;
    }

    @Override
    public Ticket searchTicket(Long ticketId, Long userId, Status status) {
        validateUser(userId);
        
        Ticket ticket = unwrapTicket(ticketId, ticketRepository.findByTicketIdAndStatus(ticketId, status));   
        
        return ticket;
    }

    @Override
    public List<AuditLog> retrieveAuditLogs(Long ticketId, Long userId) {
        // Verify user existence by checking the creator relationship
        UserServiceImp.unwrapUser(userId, ticketRepository.findCreatorByTicket(ticketId));

        return auditLogRepository.findByTicketId(ticketId);
    }

    @Override
    public void removeTicket(Long ticketId, Long userId) {
        // validate user exists
        validateUser(userId);

        // Get the ticket
        Ticket ticket = unwrapTicket(ticketId, ticketRepository.findByIdAndCreatorId(ticketId, userId));

        User user = ticket.getCreator();

        user.getTickets().remove(ticket); // remove ticket from the user's list before updating

        userRepository.save(user); // Save user to updated reference, this will trigger orphanRemoval thus no need to manually delete the ticket from tickerRepository 
    }

    // We're just validating, not actually querying and extracting the db
    /*
     * Possible to dedicate an entity validation service, that would centralize validation but nothing complex here, thus the duplication method in ticket service and comment service
     */
    private void validateUser(Long userId) {
        if(!userRepository.existsById(userId)) throw new ResourceNotFoundException(userId, User.class);
    }

    private boolean isStatusChanged(Status oldStatus, Status newStatus) {
        return newStatus != null && !oldStatus.equals(newStatus);
    }

    public static Ticket unwrapTicket(Long ticketId, Optional<Ticket> optionalTicket) {
        return optionalTicket.orElseThrow(() -> new ResourceNotFoundException(ticketId, Ticket.class));
    }
}
