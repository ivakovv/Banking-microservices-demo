package org.example.client_processing.controller;

import org.example.client_processing.config.IntegrationTestConfiguration;
import org.example.client_processing.enums.roles.UserRole;
import org.example.client_processing.model.Client;
import org.example.client_processing.model.ClientProduct;
import org.example.client_processing.model.Product;
import org.example.client_processing.model.User;
import org.example.client_processing.repository.ClientProductRepository;
import org.example.client_processing.repository.ClientRepository;
import org.example.client_processing.repository.ProductRepository;
import org.example.client_processing.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {org.example.client_processing.ClientProcessingApplication.class, IntegrationTestConfiguration.class})
@AutoConfigureWebMvc
@Transactional
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientProductRepository clientProductRepository;

    private User testUser;
    private Client testClient;
    private Product testProduct;
    private ClientProduct testClientProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setLogin("product.user");
        testUser.setPassword("encodedPassword");
        testUser.setEmail("product.user@example.com");
        testUser.setRole(UserRole.CURRENT_CLIENT);
        testUser = userRepository.save(testUser);

        testClient = new Client();
        testClient.setClientId("770100000001");
        testClient.setFirstName("Продуктовый");
        testClient.setMiddleName("Пользователь");
        testClient.setLastName("Тестов");
        testClient.setUser(testUser);
        testClient = clientRepository.save(testClient);

        testProduct = new Product();
        testProduct.setName("Дебетовая карта");
        testProduct.setKey(org.example.client_processing.enums.product.Key.DC);
        testProduct.setCreateDate(LocalDateTime.now());
        testProduct = productRepository.save(testProduct);

        testClientProduct = new ClientProduct();
        testClientProduct.setClient(testClient);
        testClientProduct.setProduct(testProduct);
        testClientProduct.setOpenDate(LocalDateTime.now());
        testClientProduct.setStatus(org.example.client_processing.enums.client_product.Status.ACTIVE);
        testClientProduct = clientProductRepository.save(testClientProduct);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void getClientProducts_WithValidClientId_ShouldReturnProducts() throws Exception {
        mockMvc.perform(get("/client-processing/api/v1/products/client/770100000001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].productName").value("Дебетовая карта"))
                .andExpect(jsonPath("$[0].productKey").value("DC"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void getClientProducts_WithNonExistentClientId_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/client-processing/api/v1/products/client/770999999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getClientProducts_WithoutAuthentication_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/client-processing/api/v1/products/client/770100000001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getClientProducts_WithWrongRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/client-processing/api/v1/products/client/770100000001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void getAllProducts_ShouldReturnAllProducts() throws Exception {
        Product creditCard = new Product();
        creditCard.setName("Кредитная карта");
        creditCard.setKey(org.example.client_processing.enums.product.Key.CC);
        creditCard.setCreateDate(LocalDateTime.now());
        productRepository.save(creditCard);

        mockMvc.perform(get("/client-processing/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].key").exists());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void getProductById_WithValidProductId_ShouldReturnProduct() throws Exception {
        mockMvc.perform(get("/client-processing/api/v1/products/" + testProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Дебетовая карта"))
                .andExpect(jsonPath("$.key").value("DC"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void getProductById_WithNonExistentProductId_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/client-processing/api/v1/products/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
