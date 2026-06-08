package com.misyakuji.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AuthControllerTest {

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
    void register_shouldCreateUser() throws Exception {
        String uniqueUsername = "test_" + System.currentTimeMillis();
        String body = """
                {"username": "%s", "password": "test123"}
                """.formatted(uniqueUsername);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("注册成功"));
    }

    @Test
    void register_duplicateUsername_shouldReturnError() throws Exception {
        String body = """
                {"username": "admin", "password": "test123"}
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() throws Exception {
        String username = "logintest_" + System.currentTimeMillis();
        String password = "testpass123";

        String registerBody = """
                {"username": "%s", "password": "%s"}
                """.formatted(username, password);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk());

        String loginBody = """
                {"username": "%s", "password": "%s"}
                """.formatted(username, password);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andReturn();

        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.token");
        assert token != null && !token.isEmpty();
    }

    @Test
    void login_withInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        String username = "nonexistent_" + System.currentTimeMillis();
        String body = """
                {"username": "%s", "password": "wrong_password"}
                """.formatted(username);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_withValidToken_shouldSucceed() throws Exception {
        String username = "logouttest_" + System.currentTimeMillis();
        String password = "testpass123";

        String registerBody = """
                {"username": "%s", "password": "%s"}
                """.formatted(username, password);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk());

        String loginBody = """
                {"username": "%s", "password": "%s"}
                """.formatted(username, password);

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        String token = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.token");

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    void logout_withoutToken_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_missingFields_shouldReturnBadRequest() throws Exception {
        String body = """
                {"username": "test"}
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("请求参数不存在"));
    }
}
