package org.example.client_processing.service.impl;

import org.example.client_processing.dto.client.ClientDto;
import org.example.client_processing.dto.client.RegistrationRequest;
import org.example.client_processing.dto.client.RegistrationResponse;
import org.example.client_processing.enums.client.DocumentType;
import org.example.client_processing.enums.roles.UserRole;
import org.example.client_processing.exception.NotFoundException;
import org.example.client_processing.mapper.ClientMapper;
import org.example.client_processing.mapper.UserMapper;
import org.example.client_processing.model.Client;
import org.example.client_processing.model.User;
import org.example.client_processing.repository.ClientRepository;
import org.example.client_processing.repository.UserRepository;
import org.example.client_processing.service.BlacklistRegistryService;
import org.example.client_processing.util.ValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private BlacklistRegistryService blacklistRegistryService;

    @InjectMocks
    private ClientServiceImpl clientService;

    private RegistrationRequest testRegistrationRequest;
    private User testUser;
    private Client testClient;
    private ClientDto testClientDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.CURRENT_CLIENT);

        testClient = new Client();
        testClient.setId(1L);
        testClient.setClientId("770100000001");
        testClient.setFirstName("Иван");
        testClient.setMiddleName("Петрович");
        testClient.setLastName("Сидоров");
        testClient.setDateOfBirth(LocalDate.of(1985, 3, 15));
        testClient.setDocumentType(DocumentType.PASSPORT);
        testClient.setDocumentId("4512345678");
        testClient.setDocumentPrefix("45");
        testClient.setDocumentSuffix("123");
        testClient.setUser(testUser);

        RegistrationRequest.UserPart userPart = new RegistrationRequest.UserPart(
                "ivan.sidorov", "password123", "ivan.sidorov@example.com"
        );
        RegistrationRequest.ClientPart clientPart = new RegistrationRequest.ClientPart(
                "770100000001", "Иван", "Петрович", "Сидоров", 
                LocalDate.of(1985, 3, 15), DocumentType.PASSPORT, 
                "4512345678", "45", "123"
        );
        testRegistrationRequest = new RegistrationRequest(userPart, clientPart);

        testClientDto = new ClientDto(
                "770100000001", "Иван", "Петрович", "Сидоров",
                LocalDate.of(1985, 3, 15), DocumentType.PASSPORT,
                "4512345678", "45", "123"
        );
    }

    @Test
    void register_WithValidData_ShouldReturnRegistrationResponse() {
        // Given
        when(clientRepository.existsByClientId("770100000001")).thenReturn(false);
        when(userRepository.existsByLogin("ivan.sidorov")).thenReturn(false);
        when(userRepository.existsByEmail("ivan.sidorov@example.com")).thenReturn(false);
        when(blacklistRegistryService.isBlacklisted(DocumentType.PASSPORT, "4512345678")).thenReturn(false);
        when(userMapper.toEntity(any())).thenReturn(testUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(clientMapper.toEntity(any(), any(User.class))).thenReturn(testClient);
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        try (MockedStatic<ValidationUtils> mockedValidation = mockStatic(ValidationUtils.class)) {
            // When
            RegistrationResponse result = clientService.register(testRegistrationRequest);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.userId());
            assertEquals("770100000001", result.clientId());
            assertEquals("testuser", result.login());
            assertEquals("test@example.com", result.email());

            verify(clientRepository).existsByClientId("770100000001");
            verify(userRepository).existsByLogin("ivan.sidorov");
            verify(userRepository).existsByEmail("ivan.sidorov@example.com");
            verify(blacklistRegistryService).isBlacklisted(DocumentType.PASSPORT, "4512345678");
            verify(passwordEncoder).encode(anyString());
            verify(userRepository).save(any(User.class));
            verify(clientRepository).save(any(Client.class));
            mockedValidation.verify(() -> ValidationUtils.validateClient(any(Client.class)));
        }
    }

    @Test
    void register_WithExistingClientId_ShouldThrowException() {
        // Given
        when(clientRepository.existsByClientId("770100000001")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> clientService.register(testRegistrationRequest)
        );

        assertEquals("A client with ID 770100000001 already exists", exception.getMessage());
        verify(clientRepository).existsByClientId("770100000001");
        verify(userRepository, never()).save(any(User.class));
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void register_WithExistingLogin_ShouldThrowException() {
        // Given
        when(clientRepository.existsByClientId("770100000001")).thenReturn(false);
        when(userRepository.existsByLogin("ivan.sidorov")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> clientService.register(testRegistrationRequest)
        );

        assertEquals("The user with loginivan.sidorovalready exists", exception.getMessage());
        verify(clientRepository).existsByClientId("770100000001");
        verify(userRepository).existsByLogin("ivan.sidorov");
        verify(userRepository, never()).save(any(User.class));
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void register_WithBlacklistedDocument_ShouldThrowException() {
        // Given
        when(clientRepository.existsByClientId("770100000001")).thenReturn(false);
        when(userRepository.existsByLogin("ivan.sidorov")).thenReturn(false);
        when(userRepository.existsByEmail("ivan.sidorov@example.com")).thenReturn(false);
        when(blacklistRegistryService.isBlacklisted(DocumentType.PASSPORT, "4512345678")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> clientService.register(testRegistrationRequest)
        );

        assertEquals("Document PASSPORT with ID 4512345678 is blacklisted and cannot be used for registration", exception.getMessage());
        verify(blacklistRegistryService).isBlacklisted(DocumentType.PASSPORT, "4512345678");
        verify(userRepository, never()).save(any(User.class));
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void getClientById_WithExistingClient_ShouldReturnClientDto() {
        // Given
        when(clientRepository.findByClientId("770100000001")).thenReturn(Optional.of(testClient));
        when(clientMapper.toResponse(testClient)).thenReturn(testClientDto);

        // When
        ClientDto result = clientService.getClientById("770100000001");

        // Then
        assertNotNull(result);
        assertEquals("770100000001", result.clientId());
        assertEquals("Иван", result.firstName());
        assertEquals("Сидоров", result.lastName());
        verify(clientRepository).findByClientId("770100000001");
        verify(clientMapper).toResponse(testClient);
    }

    @Test
    void getClientById_WithNonExistingClient_ShouldThrowNotFoundException() {
        // Given
        when(clientRepository.findByClientId("NONEXISTENT")).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> clientService.getClientById("NONEXISTENT")
        );

        assertEquals("Client with id NONEXISTENT not found", exception.getMessage());
        verify(clientRepository).findByClientId("NONEXISTENT");
        verify(clientMapper, never()).toResponse(any(Client.class));
    }

    @Test
    void getValidatedClient_WithBlacklistedClient_ShouldThrowException() {
        // Given
        when(clientRepository.findByClientId("770100000001")).thenReturn(Optional.of(testClient));
        when(blacklistRegistryService.isBlacklisted(DocumentType.PASSPORT, "4512345678")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> clientService.getValidatedClient("770100000001")
        );

        assertEquals("Document PASSPORT with ID 4512345678 is blacklisted", exception.getMessage());
        verify(blacklistRegistryService).isBlacklisted(DocumentType.PASSPORT, "4512345678");
    }

    @Test
    void getValidatedClient_WithValidClient_ShouldReturnClient() {
        // Given
        when(clientRepository.findByClientId("770100000001")).thenReturn(Optional.of(testClient));
        when(blacklistRegistryService.isBlacklisted(DocumentType.PASSPORT, "4512345678")).thenReturn(false);

        // When
        Client result = clientService.getValidatedClient("770100000001");

        // Then
        assertNotNull(result);
        assertEquals("770100000001", result.getClientId());
        assertEquals("Иван", result.getFirstName());
        assertEquals("Сидоров", result.getLastName());
        verify(blacklistRegistryService).isBlacklisted(DocumentType.PASSPORT, "4512345678");
    }
}