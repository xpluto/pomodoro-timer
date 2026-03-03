package com.pomodoro.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pomodoro.dto.CreateTagRequest;
import com.pomodoro.dto.RegisterRequest;
import com.pomodoro.dto.UpdateTagRequest;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("taguser" + System.nanoTime());
        registerRequest.setEmail("tag" + System.nanoTime() + "@test.com");
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
    void crudFlow() throws Exception {
        // Create
        CreateTagRequest createRequest = new CreateTagRequest();
        createRequest.setName("Work");
        createRequest.setColor("#FF6B6B");

        MvcResult createResult = mockMvc.perform(post("/api/tags")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createBody = objectMapper.readTree(createResult.getResponse().getContentAsString());
        assertTrue(createBody.get("success").asBoolean());
        Long tagId = createBody.get("data").get("id").asLong();
        assertEquals("Work", createBody.get("data").get("name").asText());

        // List
        MvcResult listResult = mockMvc.perform(get("/api/tags")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode listBody = objectMapper.readTree(listResult.getResponse().getContentAsString());
        assertTrue(listBody.get("success").asBoolean());
        assertTrue(listBody.get("data").isArray());
        assertTrue(listBody.get("data").size() >= 1);

        // Update
        UpdateTagRequest updateRequest = new UpdateTagRequest();
        updateRequest.setName("Study");
        updateRequest.setColor("#4ECDC4");

        MvcResult updateResult = mockMvc.perform(put("/api/tags/" + tagId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode updateBody = objectMapper.readTree(updateResult.getResponse().getContentAsString());
        assertTrue(updateBody.get("success").asBoolean());
        assertEquals("Study", updateBody.get("data").get("name").asText());

        // Delete
        mockMvc.perform(delete("/api/tags/" + tagId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        // Verify deleted - list should be empty for this user
        MvcResult afterDeleteResult = mockMvc.perform(get("/api/tags")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode afterDeleteBody = objectMapper.readTree(afterDeleteResult.getResponse().getContentAsString());
        assertEquals(0, afterDeleteBody.get("data").size());
    }
}
