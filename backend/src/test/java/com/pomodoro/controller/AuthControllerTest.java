package com.pomodoro.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pomodoro.dto.LoginRequest;
import com.pomodoro.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerThenLogin() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode registerBody = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        assertTrue(registerBody.get("success").asBoolean());
        assertNotNull(registerBody.get("data").get("accessToken").asText());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginBody = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        assertTrue(loginBody.get("success").asBoolean());
        assertNotNull(loginBody.get("data").get("accessToken").asText());
    }

    @Test
    void loginWithWrongPassword() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("anotheruser");
        registerRequest.setEmail("another@example.com");
        registerRequest.setPassword("correctpass");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("anotheruser");
        loginRequest.setPassword("wrongpass");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertFalse(body.get("success").asBoolean());
    }
}
