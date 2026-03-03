package com.pomodoro.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pomodoro.dto.RegisterRequest;
import com.pomodoro.dto.StartPomodoroRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PomodoroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("pomuser" + System.nanoTime());
        registerRequest.setEmail("pom" + System.nanoTime() + "@test.com");
        registerRequest.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        accessToken = body.get("data").get("accessToken").asText();
    }

    @Test
    void startAndCompletePomodoro() throws Exception {
        StartPomodoroRequest startRequest = new StartPomodoroRequest();
        startRequest.setPlannedDuration(1500);
        startRequest.setTaskName("Test task");

        MvcResult startResult = mockMvc.perform(post("/api/pomodoros/start")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode startBody = objectMapper.readTree(startResult.getResponse().getContentAsString());
        assertTrue(startBody.get("success").asBoolean());
        Long recordId = startBody.get("data").get("id").asLong();
        assertNotNull(recordId);

        MvcResult completeResult = mockMvc.perform(put("/api/pomodoros/" + recordId + "/complete")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode completeBody = objectMapper.readTree(completeResult.getResponse().getContentAsString());
        assertTrue(completeBody.get("success").asBoolean());
        assertEquals("completed", completeBody.get("data").get("status").asText());
    }

    @Test
    void startAndInterruptPomodoro() throws Exception {
        StartPomodoroRequest startRequest = new StartPomodoroRequest();
        startRequest.setPlannedDuration(1500);
        startRequest.setTaskName("Interrupted task");

        MvcResult startResult = mockMvc.perform(post("/api/pomodoros/start")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode startBody = objectMapper.readTree(startResult.getResponse().getContentAsString());
        Long recordId = startBody.get("data").get("id").asLong();

        MvcResult interruptResult = mockMvc.perform(put("/api/pomodoros/" + recordId + "/interrupt")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode interruptBody = objectMapper.readTree(interruptResult.getResponse().getContentAsString());
        assertTrue(interruptBody.get("success").asBoolean());
        assertEquals("interrupted", interruptBody.get("data").get("status").asText());
    }
}
