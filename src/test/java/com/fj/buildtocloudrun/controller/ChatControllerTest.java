package com.fj.buildtocloudrun.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.empty;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getChatPostWithoutJWTAnswersWith401Unauthorized() throws Exception {
        mockMvc.perform(get("/chatpost"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getChatPostWithJWTAnswersWith200AndEmptyList() throws Exception {
        mockMvc.perform(get("/chatpost")
                        .with(jwt().jwt(builder -> builder.claim("email", "testUser"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    void postChatPostWithJWTAnswersWith201AndCreatesMessage() throws Exception {
        mockMvc.perform(post("/chatpost")
                        .with(jwt().jwt(builder -> builder.claim("email", "newUser")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\": \"Hello World\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void longPollWithoutJWTAnswersWith401Unauthorized() throws Exception {
        mockMvc.perform(get("/chatpostlongpoll"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void longPollWithJWTAnswersWith200() throws Exception {
        mockMvc.perform(get("/chatpostlongpoll")
                        .with(jwt().jwt(builder -> builder.claim("email", "testUser"))))
                .andExpect(status().isOk());
    }
}
