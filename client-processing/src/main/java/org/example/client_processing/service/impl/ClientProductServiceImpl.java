package org.example.client_processing.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.client_processing.dto.card.ClientCardEventDto;
import org.example.client_processing.dto.client_product.ClientProductEventDto;
import org.example.client_processing.dto.client_product.ClientProductRequest;
import org.example.client_processing.dto.client_product.ClientProductResponse;
import org.example.client_processing.dto.client_product.ReleaseCardRequest;
import org.example.client_processing.dto.client_product.ReleaseCardResponse;
import org.example.client_processing.exception.NotFoundException;
import org.example.client_processing.kafka.ClientCardEventProducer;
import org.example.client_processing.kafka.ClientProductEventProducer;
import org.example.client_processing.mapper.CardMapper;
import org.example.client_processing.mapper.ClientProductEventMapper;
import org.example.client_processing.mapper.ClientProductMapper;
import org.example.client_processing.model.Client;
import org.example.client_processing.model.ClientProduct;
import org.example.client_processing.model.Product;
import org.example.client_processing.repository.ClientProductRepository;
import org.example.client_processing.service.ClientProductService;
import org.example.client_processing.service.ClientService;
import org.example.client_processing.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientProductServiceImpl implements ClientProductService {

    private final ClientProductMapper clientProductMapper;
    private final ClientProductEventMapper clientProductEventMapper;
    private final ClientProductRepository clientProductRepository;
    private final ClientService clientService;
    private final ProductService productService;
    private final ClientProductEventProducer clientProductEventProducer;
    private final ClientCardEventProducer clientCardEventProducer;
    private final CardMapper cardMapper;

    @Transactional
    @Override
    public ClientProductResponse create(String clientId, String productId, ClientProductRequest request) {
        Client client = clientService.getValidatedClient(clientId);

        Product product = productService.getProductByProductId(productId);

        if (clientProductRepository.existsByClientClientIdAndProductProductId(clientId, productId)) {
            throw new IllegalArgumentException(
                    String.format("Client %s already has product %s", clientId, productId));
        }

        if (clientProductRepository.existsByProductProductId(productId)) {
            throw new IllegalArgumentException(
                    String.format("Product %s is already assigned to another client", productId));
        }

        ClientProduct clientProduct = clientProductMapper.toEntity(request, client, product);

        ClientProduct savedClientProduct = clientProductRepository.save(clientProduct);

        ClientProductEventDto event = clientProductEventMapper.toCreatedEvent(savedClientProduct);
        clientProductEventProducer.sendClientProductEvent(event);

        return clientProductMapper.toResponse(savedClientProduct);
    }

    @Override
    public ClientProductResponse getByClientIdAndProductId(String clientId, String productId) {
        ClientProduct clientProduct = clientProductRepository.findByClientClientIdAndProductProductId(clientId, productId)
                .orElseThrow(() -> new NotFoundException("ClientProduct with clientId " + clientId + " and productId " + productId + " not found"));
        return clientProductMapper.toResponse(clientProduct);
    }

    @Override
    public List<ClientProductResponse> getByClientId(String clientId) {
        return clientProductRepository.findByClientClientId(clientId).stream()
                .map(clientProductMapper::toResponse)
                .toList();
    }

    @Override
    public List<ClientProductResponse> getByClientIdAndProductType(String clientId, String productType) {
        return clientProductRepository.findByClientClientIdAndProductKey(clientId, 
                org.example.client_processing.enums.product.Key.valueOf(productType)).stream()
                .map(clientProductMapper::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public ClientProductResponse updateByClientIdAndProductId(String clientId, String productId, ClientProductRequest request) {
        ClientProduct existingClientProduct = clientProductRepository.findByClientClientIdAndProductProductId(clientId, productId)
                .orElseThrow(() -> new NotFoundException("ClientProduct with clientId " + clientId + " and productId " + productId + " not found"));

        existingClientProduct.setOpenDate(request.openDate());
        existingClientProduct.setCloseDate(request.closeDate());
        existingClientProduct.setStatus(request.status());

        ClientProduct updatedClientProduct = clientProductRepository.save(existingClientProduct);

        ClientProductEventDto event = clientProductEventMapper.toUpdatedEvent(updatedClientProduct);
        clientProductEventProducer.sendClientProductEvent(event);

        return clientProductMapper.toResponse(updatedClientProduct);
    }

    @Transactional
    @Override
    public void deleteByClientIdAndProductId(String clientId, String productId) {
        ClientProduct clientProduct = clientProductRepository.findByClientClientIdAndProductProductId(clientId, productId)
                .orElseThrow(() -> new NotFoundException("ClientProduct with clientId " + clientId + " and productId " + productId + " not found"));

        ClientProductEventDto event = clientProductEventMapper.toDeletedEvent(clientProduct);
        clientProductEventProducer.sendClientProductEvent(event);

        clientProductRepository.delete(clientProduct);
    }

    @Override
    public ReleaseCardResponse releaseCard(String clientId, ReleaseCardRequest request) {
        Client client = clientService.getValidatedClient(clientId);

        ClientCardEventDto cardEvent = cardMapper.toClientCardEvent(client, request);

        clientCardEventProducer.sendCardCreationRequest(cardEvent);

        return cardMapper.toResponse("Заявка на создание карты принята в обработку");
    }
}