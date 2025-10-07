//package com.codelogium.ticketing;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import com.codelogium.ticketing.dto.TicketCreationRequest;
//import com.codelogium.ticketing.repository.*;
//import com.codelogium.ticketing.service.FileStorageService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks; // <-- Use InjectMocks
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension; // <-- Use Mockito Extension
//
//import com.codelogium.ticketing.dto.TicketInfoUpdateDTO;
//import com.codelogium.ticketing.dto.TicketStatusUpdateDTO;
//import com.codelogium.ticketing.entity.AuditLog;
//import com.codelogium.ticketing.entity.Ticket;
//import com.codelogium.ticketing.entity.User;
//import com.codelogium.ticketing.entity.enums.Category;
//import com.codelogium.ticketing.entity.enums.Priority;
//import com.codelogium.ticketing.entity.enums.Status;
//import com.codelogium.ticketing.entity.enums.UserRole;
//import com.codelogium.ticketing.exception.ResourceNotFoundException;
//import com.codelogium.ticketing.service.TicketServiceImp; // <-- Inject the implementation class
//
//
//@ExtendWith(MockitoExtension.class) // <-- Correct extension for Mockito-based unit tests
//public class TicketServiceTest {
//
//    // Inject the mocks into the actual implementation class
//    @InjectMocks
//    private TicketServiceImp ticketService;
//
//    // All dependencies of TicketServiceImp are mocked
//    @Mock
//    private TicketRepository ticketRepository;
//
//    @Mock
//    private AuditLogRepository auditLogRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private TicketRoomRepository ticketRoomRepository;
//
//    @Mock
//    private RoomRepository roomRepository;
//
//    @Mock
//    private FileStorageService fileStorageService;
//
//
//    private User testUser;
//    private Ticket testTicket;
//    private AuditLog testAuditLog;
//
//    // --- REMOVED FAILING CONSTRUCTOR ---
//    // The large constructor is removed because @InjectMocks handles dependency setup
//    // and entities should not be injected as parameters.
//
//
//    @BeforeEach
//    void setUp() throws Exception {
//        // @InjectMocks handles this initialization now:
//        // ticketService = new TicketServiceImp(ticketRepository, userRepository, auditLogRepository, roomRepository, ticketRoomRepository, fileStorageService);
//
//        // Instantiating test fixtures
//        testUser = new User(1L, "tupac", "tupac123", "tupac@gmail.com", UserRole.RENTER, null, null, null);
//
//        // Note: The Ticket constructor needs to be updated to accept the User object
//        testTicket = new Ticket(1L, "Discrepancy while login", "Error 500 keeps pop up while password is correct",
//                Instant.now(), Status.NEW, Category.INTERNET, Priority.HIGH, testUser, null, null);
//
//        testAuditLog = new AuditLog(1L, testTicket.getId(), null, testUser.getId(), "TICKET_CREATED", null,
//                testTicket.getStatus().toString(), Instant.now());
//    }
//
//    /**
//     * Helper to mock basic repository calls. Uses lenient() to avoid UnnecessaryStubbing exceptions.
//     */
//    private void mockBasicUserAndTicketRepo() {
//        // Use lenient() because these mocks might not be called in every single test method.
//        lenient().when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
//        lenient().when(userRepository.existsById(testUser.getId())).thenReturn(true);
//        lenient().when(ticketRepository.findByIdAndCreatorId(testTicket.getId(), testUser.getId()))
//                .thenReturn(Optional.of(testTicket));
//    }
//
////    @Test
////    void shouldAddTicketSuccessfully() {
////        // Mock
////        // Status is null in order to test if it being set while ticket saving
////        TicketCreationRequest ticketToCreate = new TicketCreationRequest(testTicket.getId(), "Discrepancy while login",
////                "Error 500 keeps pop up while password is correct", null, Category.INTERNET,
////                Priority.HIGH, testUser, null, null);
////
////        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
////        // Must mock the service behavior correctly
////        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
////            Ticket t = invocation.getArgument(0);
////            t.setId(1L); // Simulate saving and getting an ID
////            return t;
////        });
////
////        // Act
////        Ticket result = ticketService.createTicket(testUser.getId(), ticketToCreate);
////
////        // Assert
////        assertNotNull(result);
////        assertEquals("Discrepancy while login", result.getTitle());
////        assertEquals(Status.NEW, result.getStatus()); // Assure that status was set during ticket creation
////        verify(ticketRepository, times(1)).save(any(Ticket.class));
////    }
//
//    @Test
//    void shouldRetrieveTicketSuccessfully() {
//        // Mock
//        // Explicitly mocking the required dependencies for this test to pass the permission/existence checks.
//        when(userRepository.existsById(testUser.getId())).thenReturn(true);
//        when(ticketRepository.findByIdAndCreatorId(testTicket.getId(), testUser.getId()))
//                .thenReturn(Optional.of(testTicket));
//
//        // Act
//        // Changed to use testUser.getId() instead of hardcoded 1L for creator
//        Ticket result = ticketService.retrieveTicket(testTicket.getId(), testUser.getId());
//
//        // Assert
//        assertEquals(testTicket.getId(), result.getId());
//        assertEquals(testTicket.getTitle(), result.getTitle());
//    }
//
//    @Test
//    void shouldUpdateTicketInfoSuccessfully() {
//        // Mock
//        when(userRepository.existsById(testUser.getId())).thenReturn(true);
//        when(ticketRepository.findByIdAndCreatorId(testTicket.getId(), testUser.getId()))
//                .thenReturn(Optional.of(testTicket));
//
//        TicketInfoUpdateDTO dto = new TicketInfoUpdateDTO("Can't Login even if password is correct", null, null,
//                Category.OTHER, Priority.MEDIUM);
//
//        // Mock the save operation (usually returns the saved entity)
//        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//
//        // Act
//        Ticket result = ticketService.updateTicketInfo(testTicket.getId(), testUser.getId(), dto);
//
//        // assert
//        assertEquals(dto.getTitle(), result.getTitle());
//        assertEquals(testTicket.getDescription(), result.getDescription());
//        assertEquals(dto.getCategory(), result.getCategory());
//        assertEquals(dto.getPriority(), result.getPriority());
//    }
//
//    @Test
//    void shouldUpdateTicketStatusSuccessfully() {
//        // Mock
//        when(userRepository.existsById(testUser.getId())).thenReturn(true);
//        when(ticketRepository.findByIdAndCreatorId(testTicket.getId(), testUser.getId()))
//                .thenReturn(Optional.of(testTicket));
//
//        TicketStatusUpdateDTO dto = new TicketStatusUpdateDTO(Status.IN_PROGRESS);
//
//        // Mock the save operation
//        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act
//        Ticket result = ticketService.updateTicketStatus(testTicket.getId(), testUser.getId(), dto);
//
//        // assert
//        assertEquals(dto.getStatus(), result.getStatus());
//        assertEquals(testTicket.getTitle(), result.getTitle());
//    }
//
//    @Test
//    void shouldRemoveTicketSuccessfully() {
//        // Mock
//        mockBasicUserAndTicketRepo(); // Can still use this helper, but relies on lenient()
//
//        // Ensure ticket is found and the user has a mutable list of tickets
//        testUser.setTickets(new ArrayList<>(List.of(testTicket)));
//
//        // Explicitly mock the dependencies for this specific test
//        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
//        when(ticketRepository.findByIdAndCreatorId(testTicket.getId(), testUser.getId()))
//                .thenReturn(Optional.of(testTicket));
//
//        // Mock the user save operation
//        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act
//        ticketService.removeTicket(testTicket.getId(), testUser.getId());
//
//        // Assert
//        // Check if the ticket was removed from the user's list
//        assertFalse(testUser.getTickets().contains(testTicket));
//        // Verify user save was called (to persist the user's updated ticket list)
//        verify(userRepository, times(1)).save(testUser);
//        // Verify ticket delete was NOT called (as deletion is handled by orphanRemoval on the User entity)
//        verify(ticketRepository, never()).delete(any());
//    }
//
//    @Test
//    void shouldSearchTicketSuccessfully() {
//        // Mock
//        when(userRepository.existsById(testUser.getId())).thenReturn(true);
//        when(ticketRepository.findByTicketIdAndStatus(testTicket.getId(), Status.NEW))
//                .thenReturn(Optional.of(testTicket));
//
//        // Act
//        Ticket result = ticketService.searchTicket(testTicket.getId(), testUser.getId(), Status.NEW);
//
//        // Assert
//        assertEquals(testTicket.getId(), result.getId());
//        assertEquals(testTicket.getStatus(), result.getStatus());
//        assertEquals(testTicket.getCreator().getRole(), result.getCreator().getRole());
//    }
//
//    @Test
//    void shouldRetrieveAuditLogSuccessfully() {
//        // Mock
//        when(ticketRepository.findCreatorByTicket(testTicket.getId())).thenReturn(Optional.of(testUser));
//        when(auditLogRepository.findByTicketId(testTicket.getId())).thenReturn(List.of(testAuditLog));
//
//        // Act
//        List<AuditLog> results = ticketService.retrieveAuditLogs(testTicket.getId(), testUser.getId());
//
//        // Assert
//        assertEquals(1, results.size());
//
//        AuditLog result = results.get(0);
//        assertEquals(testAuditLog.getAction(), result.getAction());
//        assertEquals(testAuditLog.getTicketId(), result.getTicketId());
//        assertEquals(testAuditLog.getUserId(), result.getUserId());
//    }
//
//    @Test
//    void shouldRetrieveTicketsByCreatorSuccessfully() {
//        // Mock
//        when(userRepository.existsById(testUser.getId())).thenReturn(true);
//        when(ticketRepository.findByCreatorId(testUser.getId())).thenReturn(List.of(testTicket));
//
//        // Act
//        List<Ticket> results = ticketService.retrieveTicketsByCreator(testUser.getId());
//
//        // Assert
//        assertEquals(1, results.size());
//
//        Ticket result = results.get(0);
//        assertEquals(testTicket.getStatus(), result.getStatus());
//        assertEquals(testTicket.getCreator(), result.getCreator());
//        assertEquals(testTicket.getId(), result.getId());
//    }
//
//    /* NON SUCCESSFUL CALLS */
//
//    @Test
//    void shouldFailRetrievingTicketsByCreator() throws Exception {
//        // Mock
//        when(userRepository.existsById(testUser.getId())).thenReturn(true);
//        // Mock the service's internal dependency (repository) to return empty list
//        when(ticketRepository.findByCreatorId(testUser.getId())).thenReturn(List.of());
//
//        // Act & Assert
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,() -> ticketService.retrieveTicketsByCreator(testUser.getId()));
//
//        // Assert the expected exception message (if defined by the service logic)
//        String expectedMessage = "No tickets created yet.";
//        String actualMessage = exception.getMessage();
//
//        assertTrue(actualMessage.contains(expectedMessage));
//
//    }
//}
