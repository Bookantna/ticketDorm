package com.codelogium.ticketing.mapper;

import com.codelogium.ticketing.dto.*;
import com.codelogium.ticketing.entity.Ticket;
import com.codelogium.ticketing.entity.Comment;
import com.codelogium.ticketing.entity.TicketRoom;
import com.codelogium.ticketing.entity.Room;
import com.codelogium.ticketing.entity.User;

import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TicketMapper {

    /**
     * Converts a JPA Ticket entity into a safe, non-recursive TicketDTO for API responses.
     * This method ensures nested entities (User, Comments, Rooms) are also mapped to simplified DTOs,
     * thereby preventing infinite JSON recursion errors.
     * @param ticket The JPA entity.
     * @return The DTO object.
     */
    public TicketDTO toDto(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setImageUrl(ticket.getImageUrl());
        dto.setCreationDate(ticket.getCreationDate());
        dto.setStatus(ticket.getStatus());
        dto.setCategory(ticket.getCategory());
        dto.setPriority(ticket.getPriority());

        // Map the creator (User) to a simple UserDTO
        if (ticket.getCreator() != null) {
            dto.setCreator(mapUserToSimpleDto(ticket.getCreator()));
        }

        // Map the Comment collection
        if (ticket.getComments() != null) {
            dto.setComments(ticket.getComments().stream()
                    .map(this::mapCommentToDto)
                    .collect(Collectors.toList()));
        }

        // Map the TicketRoom collection (room associations)
        if (ticket.getRoomAssociations() != null) {
            dto.setRoomAssociations(ticket.getRoomAssociations().stream()
                    .map(this::mapTicketRoomToDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    // --- Private mapping methods for nested DTOs ---

    /**
     * Maps User entity to a minimal DTO, breaking the recursion with Ticket/Comment.
     * FIX: Changed return type from UserReferenceDTO to UserDTO.
     */
    private UserDTO mapUserToSimpleDto(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        // Important: Do NOT map the tickets, comments, or memberships collections!

        return dto;
    }

    /** Maps Comment entity to DTO, including the Author DTO. */
    private CommentDTO mapCommentToDto(Comment comment) {
        if (comment == null) return null;

        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());

        // FIX: Assuming the Comment entity uses getText() for its content field.
        dto.setContent(comment.getContent());

        // Assuming Comment entity has a 'createdAt' field.
        // dto.setCreatedAt(comment.getCreatedAt());

        if (comment.getAuthor() != null) {
            // FIX: Changed setAuthor() to setCreator() to match the CommentDTO.
            dto.setCreator(mapUserToSimpleDto(comment.getAuthor()));
        }

        // Important: Do NOT map the 'ticket' field back to prevent recursion.
        return dto;
    }

    /** Maps TicketRoom join entity to DTO, including the Room DTO. */
    private TicketRoomDTO mapTicketRoomToDto(TicketRoom ticketRoom) {
        if (ticketRoom == null) return null;

        TicketRoomDTO dto = new TicketRoomDTO();
        dto.setId(ticketRoom.getId());

        // Map the associated Room to a simple RoomDTO
        if (ticketRoom.getRoom() != null) {
            dto.setRoom(mapRoomToSimpleDto(ticketRoom.getRoom()));
        }

        // Important: Do NOT map the 'ticket' field back to prevent recursion.
        return dto;
    }

    /** Maps Room entity to a minimal DTO, breaking the recursion with TicketRoom. */
    private RoomDTO mapRoomToSimpleDto(Room room) {
        if (room == null) return null;

        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        // Assuming Room has a name field to display contextually
        dto.setName(room.getRoomNumber());

        // Important: Do NOT map the memberships or ticketAssociations fields!
        return dto;
    }
}
