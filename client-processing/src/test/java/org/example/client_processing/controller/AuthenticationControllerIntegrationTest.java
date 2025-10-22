package org.example.client_processing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.client_processing.config.IntegrationTestConfiguration;
import org.example.client_processing.dto.auth.LoginRequest;
import org.example.client_processing.enums.roles.UserRole;
import org.example.client_processing.model.User;
import org.example.client_processing.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {org.example.client_processing.ClientProcessingApplication.class, IntegrationTestConfiguration.class})
@AutoConfigureWebMvc
@Transactional
@ActiveProfiles("test")
class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setLogin("test.user");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setEmail("test.user@example.com");
        testUser.setRole(UserRole.CURRENT_CLIENT);
        testUser = userRepository.save(testUser);
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test.user", "password123");

        mockMvc.perform(post("/client-processing/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").exists());
    }

    @Test
    void login_WithInvalidUsername_ShouldReturn401() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistent.user", "password123");

        mockMvc.perform(post("/client-processing/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void login_WithInvalidPassword_ShouldReturn401() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test.user", "wrongpassword");

        mockMvc.perform(post("/client-processing/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void login_WithEmptyCredentials_ShouldReturn400() throws Exception {
        LoginRequest loginRequest = new LoginRequest("", "");

        mockMvc.perform(post("/client-processing/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithNullCredentials_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/client-processing/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithBlockedUser_ShouldReturn403() throws Exception {
        User blockedUser = new User();
        blockedUser.setLogin("blocked.user");
        blockedUser.setPassword(passwordEncoder.encode("password123"));
        blockedUser.setEmail("blocked.user@example.com");
        blockedUser.setRole(UserRole.BLOCKED_CLIENT);
        userRepository.save(blockedUser);

        LoginRequest loginRequest = new LoginRequest("blocked.user", "password123");

        mockMvc.perform(post("/client-processing/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User is blocked"));
    }
}
