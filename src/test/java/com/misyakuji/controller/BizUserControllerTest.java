package com.misyakuji.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WithMockUser(roles = "ADMIN")
class BizUserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void getAll_shouldReturnUserList() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))));
    }

    @Test
    void getById_withValidId_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").isString());
    }

    @Test
    void getById_withInvalidId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/users/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByUsername_withValidName_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/users/username/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void getByUsername_withInvalidName_shouldReturn404() throws Exception {
        mockMvc.perform(get("/users/username/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByPermissionLevel_shouldReturnFilteredList() throws Exception {
        mockMvc.perform(get("/users/permission/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))))
                .andExpect(jsonPath("$[0].permissionLevel").value(3));
    }

    @Test
    void getByStatus_shouldReturnFilteredList() throws Exception {
        mockMvc.perform(get("/users/status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))));
    }

    @Test
    void searchUsers_shouldReturnMatchingResults() throws Exception {
        mockMvc.perform(get("/users/search").param("keyword", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))));
    }

    @Test
    void getActiveUsers_shouldReturnList() throws Exception {
        mockMvc.perform(get("/users/active"))
                .andExpect(status().isOk());
    }

    @Test
    void checkUserExists_withExistingId_shouldReturnTrue() throws Exception {
        mockMvc.perform(get("/users/1/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void checkUserExists_withInvalidId_shouldReturnFalse() throws Exception {
        mockMvc.perform(get("/users/99999/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void checkUsernameExists_withExistingName_shouldReturnTrue() throws Exception {
        mockMvc.perform(get("/users/username/admin/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void create_shouldReturnCreatedUser() throws Exception {
        String uniqueUsername = "new_user_" + System.currentTimeMillis();
        String body = """
                {"username": "%s", "passwordHash": "test123", "permissionLevel": 1, "status": 1}
                """.formatted(uniqueUsername);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(uniqueUsername));
    }

    @Test
    void update_shouldReturnUpdatedUser() throws Exception {
        String uniqueUsername = "update_test_" + System.currentTimeMillis();
        String createBody = """
                {"username": "%s", "passwordHash": "test123", "permissionLevel": 1, "status": 1}
                """.formatted(uniqueUsername);

        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int newId = com.jayway.jsonpath.JsonPath.read(response, "$.userId");

        String updateBody = """
                {"username": "%s_updated", "status": 1}
                """.formatted(uniqueUsername);

        mockMvc.perform(put("/users/" + newId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(newId));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        String uniqueUsername = "delete_test_" + System.currentTimeMillis();
        String createBody = """
                {"username": "%s", "passwordHash": "test123", "permissionLevel": 1, "status": 1}
                """.formatted(uniqueUsername);

        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int newId = com.jayway.jsonpath.JsonPath.read(response, "$.userId");

        mockMvc.perform(delete("/users/" + newId))
                .andExpect(status().isNoContent());
    }
}
