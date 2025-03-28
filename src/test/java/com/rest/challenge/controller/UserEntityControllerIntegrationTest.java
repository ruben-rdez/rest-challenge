package com.rest.challenge.controller;

import com.rest.challenge.model.UserEntityDTO;
import com.rest.challenge.service.UserEntityService;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserEntityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserEntityService userEntityService;

    @Before
    public void setUp(){
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(UserEntityController.class)
            .build();
    }

    @Test
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/users")
            .param("page", "0")
            .param("size", "1")
            .param("sortBy", "id")
            .param("sortDirection", "asc")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", hasSize(1)));
     }

    @Test
    void testGetUserById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists());
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/users/100")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
    }

    @Test
    void testSaveUser() throws Exception{
        UserEntityDTO userDTO = new UserEntityDTO();
        userDTO.setName("tutsis");
        userDTO.setEmail("tutsis@mail.com");

        mockMvc.perform(MockMvcRequestBuilders
            .post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDTO))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
            .delete("/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateUser() throws Exception{
        UserEntityDTO userDTO = new UserEntityDTO();
        userDTO.setName("tutsis");
        userDTO.setEmail("tutsis@example.com");

        mockMvc.perform(MockMvcRequestBuilders
            .put("/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDTO))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }
}
