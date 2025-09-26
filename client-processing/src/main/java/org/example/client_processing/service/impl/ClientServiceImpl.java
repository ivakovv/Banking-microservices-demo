package org.example.client_processing.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.client_processing.dto.client.ClientDto;
import org.example.client_processing.dto.client.RegistrationRequest;
import org.example.client_processing.dto.client.RegistrationResponse;
import org.example.client_processing.mapper.ClientMapper;
import org.example.client_processing.mapper.UserMapper;
import org.example.client_processing.model.Client;
import org.example.client_processing.model.User;
import org.example.client_processing.repository.ClientRepository;
import org.example.client_processing.repository.UserRepository;
import org.example.client_processing.service.BlacklistRegistryService;
import org.example.client_processing.service.ClientService;
import org.example.client_processing.util.ValidationUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    private final UserRepository userRepository;

    private final ClientMapper clientMapper;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final BlacklistRegistryService blacklistRegistryService;


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public RegistrationResponse register(RegistrationRequest request) {
        if (clientRepository.existsByClientId(request.client().clientId()))
            throw new IllegalArgumentException("A client with ID " + request.client().clientId() + " already exists");
        if (userRepository.existsByLogin(request.user().login())) {
            throw new IllegalArgumentException("The user with login" + request.user().login() + "already exists");
        }
        if (userRepository.existsByEmail(request.user().email())) {
            throw new IllegalArgumentException("The user with email" + request.user().email() + "already exists");
        }

        if (blacklistRegistryService.isBlacklisted(request.client().documentType(), request.client().documentId())) {
            throw new IllegalArgumentException(
                String.format("Document %s with ID %s is blacklisted and cannot be used for registration", 
                    request.client().documentType(), request.client().documentId()));
        }

        User user = userMapper.toEntity(request.user());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        Client client = clientMapper.toEntity(request.client(), user);
        ValidationUtils.validateClient(client);

        Client savedClient = clientRepository.save(client);

        return new RegistrationResponse(
                savedUser.getId(),
                savedClient.getClientId(),
                savedUser.getLogin(),
                savedUser.getEmail()
        );
    }

    @Override
    public ClientDto getClientById(String clientId) {
        return clientMapper.toResponse(clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client with id " + clientId + " not found")));
    }

    @Override
    public Client getClientEntityById(String clientId) {
        return clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client with id " + clientId + " not found"));
    }
}
