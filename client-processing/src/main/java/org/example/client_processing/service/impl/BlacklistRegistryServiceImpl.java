package org.example.client_processing.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.client_processing.dto.blacklist.BlacklistRegistryRequest;
import org.example.client_processing.dto.blacklist.BlacklistRegistryResponse;
import org.example.client_processing.enums.client.DocumentType;
import org.example.client_processing.exception.NotFoundException;
import org.example.client_processing.mapper.BlacklistRegistryMapper;
import org.example.client_processing.model.BlacklistRegistry;
import org.example.client_processing.repository.BlacklistRegistryRepository;
import org.example.client_processing.service.BlacklistRegistryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BlacklistRegistryServiceImpl implements BlacklistRegistryService {

    private final BlacklistRegistryRepository blacklistRegistryRepository;
    private final BlacklistRegistryMapper blacklistRegistryMapper;

    @Override
    @Transactional
    public BlacklistRegistryResponse addToBlackList(BlacklistRegistryRequest request) {
        if (blacklistRegistryRepository.existsByDocumentId(request.documentId())) {
            throw new IllegalArgumentException(
                "Document with id" + request.documentId() + " is already in blacklist");
        }

        BlacklistRegistry blacklistRegistry = blacklistRegistryMapper.toEntity(request);
        
        if (request.blacklistedAt() != null) {
            blacklistRegistry.setBlacklistedAt(request.blacklistedAt());
        }

        BlacklistRegistry savedRegistry = blacklistRegistryRepository.save(blacklistRegistry);
        
        return blacklistRegistryMapper.toResponse(savedRegistry);
    }

    @Override
    @Transactional
    public void deleteFromBlackList(String documentId) {
        BlacklistRegistry blacklistRegistry = blacklistRegistryRepository
                .findByDocumentId(documentId)
                .orElseThrow(() -> new NotFoundException(
                "Document with id" + documentId + " is not found in blacklist"));
        
        blacklistRegistryRepository.delete(blacklistRegistry);
    }

    @Override
    @Transactional
    public BlacklistRegistryResponse changeExpirationDate(String documentId, LocalDateTime expirationDate) {
        BlacklistRegistry blacklistRegistry = blacklistRegistryRepository
                .findByDocumentId(documentId)
                .orElseThrow(() -> new NotFoundException(
                        "Document with id" + documentId + " is not found in blacklist"));
        
        blacklistRegistry.setBlacklistExpirationDate(expirationDate);
        BlacklistRegistry updatedRegistry = blacklistRegistryRepository.save(blacklistRegistry);
        
        return blacklistRegistryMapper.toResponse(updatedRegistry);
    }

    @Override
    public BlacklistRegistryResponse getBlacklistRegistry(String documentId) {
        BlacklistRegistry blacklistRegistry = blacklistRegistryRepository
                .findByDocumentId(documentId)
                .orElseThrow(() -> new NotFoundException(
                    "Document with ID" + documentId + " is not found in blacklist"));
        
        return blacklistRegistryMapper.toResponse(blacklistRegistry);
    }

    @Override
    public boolean isBlacklisted(DocumentType documentType, String documentId) {
        return blacklistRegistryRepository.isDocumentBlacklisted(
            documentType, 
            documentId, 
            LocalDateTime.now()
        );
    }
}
