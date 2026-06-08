package com.misyakuji.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class DemoControllerTest {

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
    @WithMockUser
    void publicAccess_shouldReturnMessage() throws Exception {
        mockMvc.perform(get("/demo/public"))
                .andExpect(status().isOk())
                .andExpect(content().string("公开接口，谁都能访问"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminOnly_withAdminRole_shouldReturnMessage() throws Exception {
        mockMvc.perform(get("/demo/admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("只有ADMIN角色能访问"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminOnly_withUserRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/demo/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userOnly_withUserRole_shouldReturnMessage() throws Exception {
        mockMvc.perform(get("/demo/user"))
                .andExpect(status().isOk())
                .andExpect(content().string("只有USER角色能访问"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void userOnly_withAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/demo/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void bothRoles_withUserRole_shouldReturnMessage() throws Exception {
        mockMvc.perform(get("/demo/both"))
                .andExpect(status().isOk())
                .andExpect(content().string("ADMIN和USER都能访问"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void bothRoles_withAdminRole_shouldReturnMessage() throws Exception {
        mockMvc.perform(get("/demo/both"))
                .andExpect(status().isOk())
                .andExpect(content().string("ADMIN和USER都能访问"));
    }

    @Test
    void withoutAuth_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/demo/public"))
                .andExpect(status().isUnauthorized());
    }
}
