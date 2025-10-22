package org.example.client_processing.service.impl;

import org.example.client_processing.enums.roles.UserRole;
import org.example.client_processing.model.User;
import org.example.client_processing.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientBlockingServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClientBlockingServiceImpl clientBlockingService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("anna.petrova");
        testUser.setPassword("securePass123");
        testUser.setEmail("anna.petrova@example.com");
        testUser.setRole(UserRole.CURRENT_CLIENT);
    }

    @Test
    void blockClient_WithValidClient_ShouldBlockSuccessfully() {
        // Given
        String clientId = "770100000001";
        String reason = "Suspicious activity detected";
        when(userRepository.findByClientClientId(clientId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        clientBlockingService.blockClient(clientId, reason);

        // Then
        assertEquals(UserRole.BLOCKED_CLIENT, testUser.getRole());
        verify(userRepository).findByClientClientId(clientId);
        verify(userRepository).save(testUser);
    }

    @Test
    void blockClient_WithNonExistingClient_ShouldThrowException() {
        // Given
        String clientId = "NONEXISTENT";
        String reason = "Suspicious activity detected";
        when(userRepository.findByClientClientId(clientId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> clientBlockingService.blockClient(clientId, reason)
        );

        assertEquals("Client with ID NONEXISTENT not found", exception.getMessage());
        verify(userRepository).findByClientClientId(clientId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void unblockClient_WithValidClient_ShouldUnblockSuccessfully() {
        // Given
        String clientId = "770100000001";
        testUser.setRole(UserRole.BLOCKED_CLIENT);
        when(userRepository.findByClientClientId(clientId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        clientBlockingService.unblockClient(clientId);

        // Then
        assertEquals(UserRole.CURRENT_CLIENT, testUser.getRole());
        verify(userRepository).findByClientClientId(clientId);
        verify(userRepository).save(testUser);
    }

    @Test
    void unblockClient_WithNonExistingClient_ShouldThrowException() {
        // Given
        String clientId = "NONEXISTENT";
        when(userRepository.findByClientClientId(clientId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> clientBlockingService.unblockClient(clientId)
        );

        assertEquals("Client with ID NONEXISTENT not found", exception.getMessage());
        verify(userRepository).findByClientClientId(clientId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void isClientBlocked_WithBlockedClient_ShouldReturnTrue() {
        // Given
        String clientId = "770100000001";
        testUser.setRole(UserRole.BLOCKED_CLIENT);
        when(userRepository.findByClientClientId(clientId)).thenReturn(Optional.of(testUser));

        // When
        boolean result = clientBlockingService.isClientBlocked(clientId);

        // Then
        assertTrue(result);
        verify(userRepository).findByClientClientId(clientId);
    }

    @Test
    void isClientBlocked_WithActiveClient_ShouldReturnFalse() {
        // Given
        String clientId = "770100000001";
        testUser.setRole(UserRole.CURRENT_CLIENT);
        when(userRepository.findByClientClientId(clientId)).thenReturn(Optional.of(testUser));

        // When
        boolean result = clientBlockingService.isClientBlocked(clientId);

        // Then
        assertFalse(result);
        verify(userRepository).findByClientClientId(clientId);
    }

    @Test
    void isClientBlocked_WithNonExistingClient_ShouldReturnFalse() {
        // Given
        String clientId = "NONEXISTENT";
        when(userRepository.findByClientClientId(clientId)).thenReturn(Optional.empty());

        // When
        boolean result = clientBlockingService.isClientBlocked(clientId);

        // Then
        assertFalse(result);
        verify(userRepository).findByClientClientId(clientId);
    }

    @Test
    void blockClient_WithAlreadyBlockedClient_ShouldRemainBlocked() {
        // Given
        String clientId = "770100000001";
        String reason = "Additional security check";
        testUser.setRole(UserRole.BLOCKED_CLIENT);
        when(userRepository.findByClientClientId(clientId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        clientBlockingService.blockClient(clientId, reason);

        // Then
        assertEquals(UserRole.BLOCKED_CLIENT, testUser.getRole());
        verify(userRepository).findByClientClientId(clientId);
        verify(userRepository).save(testUser);
    }

    @Test
    void unblockClient_WithAlreadyActiveClient_ShouldRemainActive() {
        // Given
        String clientId = "770100000001";
        testUser.setRole(UserRole.CURRENT_CLIENT);
        when(userRepository.findByClientClientId(clientId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        clientBlockingService.unblockClient(clientId);

        // Then
        assertEquals(UserRole.CURRENT_CLIENT, testUser.getRole());
        verify(userRepository).findByClientClientId(clientId);
        verify(userRepository).save(testUser);
    }
}