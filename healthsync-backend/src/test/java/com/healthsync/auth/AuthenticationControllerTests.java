package com.healthsync.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthsync.auth.controller.AuthenticationController;
import com.healthsync.auth.dto.AuthResponse;
import com.healthsync.auth.dto.RegisterRequest;
import com.healthsync.auth.entity.Role;
import com.healthsync.auth.service.AuthenticationService;
import com.healthsync.auth.service.UserService;
import com.healthsync.security.JwtAuthenticationEntryPoint;
import com.healthsync.security.JwtService;
import com.healthsync.security.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller unit tests validating validation and authentication mock flows.
 */
@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    public void registerUser_Success() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "testuser",
                "password123",
                "testuser@healthsync.com",
                "Test User",
                Role.USER
        );

        AuthResponse authResponse = new AuthResponse(
                "accessToken123",
                "refreshToken123",
                "Bearer",
                "testuser",
                "USER"
        );

        Mockito.when(authenticationService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.accessToken").value("accessToken123"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    public void registerUser_InvalidInput_ReturnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "",
                "123",
                "not-an-email",
                "",
                null
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}
