package org.example.client_processing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client_processing.enums.roles.UserRole;
import org.example.client_processing.model.User;
import org.example.client_processing.repository.UserRepository;
import org.example.client_processing.service.ClientBlockingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ClientBlockingServiceImpl implements ClientBlockingService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void blockClient(String clientId, String reason) {
        User user = userRepository.findByClientClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client with ID " + clientId + " not found"));
        
        user.setRole(UserRole.BLOCKED_CLIENT);
        userRepository.save(user);
        
        log.warn("Client {} blocked. Reason: {}", clientId, reason);
    }

    @Override
    @Transactional
    public void unblockClient(String clientId) {
        User user = userRepository.findByClientClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client with ID " + clientId + " not found"));
        
        user.setRole(UserRole.CURRENT_CLIENT);
        userRepository.save(user);
        
        log.info("Client {} unblocked", clientId);
    }

    @Override
    public boolean isClientBlocked(String clientId) {
        return userRepository.findByClientClientId(clientId)
                .map(user -> user.getRole() == UserRole.BLOCKED_CLIENT)
                .orElse(false);
    }
}
