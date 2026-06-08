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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WithMockUser(username = "admin", roles = "ADMIN")
class BizUserInfoControllerTest {

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
    void getAll_shouldReturnInfoList() throws Exception {
        mockMvc.perform(get("/user-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))));
    }

    @Test
    void getByUserId_withValidId_shouldReturnInfo() throws Exception {
        mockMvc.perform(get("/user-info/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.infoId").isNumber());
    }

    @Test
    void getByUserId_withInvalidId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/user-info/user/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByPhone_shouldReturnInfo() throws Exception {
        mockMvc.perform(get("/user-info/phone/13800138000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("13800138000"));
    }

    @Test
    void getByEmail_shouldReturnInfo() throws Exception {
        mockMvc.perform(get("/user-info/email/admin@company.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@company.com"));
    }

    @Test
    void getByDepartment_shouldReturnList() throws Exception {
        mockMvc.perform(get("/user-info/department/技术部"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))));
    }

    @Test
    void getByPosition_shouldReturnList() throws Exception {
        mockMvc.perform(get("/user-info/position/前端开发工程师"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))));
    }

    @Test
    void getByStatus_shouldReturnList() throws Exception {
        mockMvc.perform(get("/user-info/status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))));
    }

    @Test
    void searchByRealName_shouldReturnResults() throws Exception {
        mockMvc.perform(get("/user-info/search/张三"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))));
    }

    @Test
    void getCurrentUserInfo_shouldReturnUserWithInfo() throws Exception {
        mockMvc.perform(get("/user-info/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user").exists())
                .andExpect(jsonPath("$.data.userInfo").exists());
    }

    @Test
    void create_shouldReturnCreatedInfo() throws Exception {
        String uniqueUsername = "infotest_" + System.currentTimeMillis();
        String userBody = """
                {"username": "%s", "passwordHash": "test123", "permissionLevel": 1, "status": 1}
                """.formatted(uniqueUsername);

        String userResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int newUserId = com.jayway.jsonpath.JsonPath.read(userResponse, "$.userId");
        
        String body = """
                {"bizUser": {"userId": %d}, "realName": "测试", "phone": "1390000%d", "email": "test%d@test.com", "department": "测试部", "position": "测试工程师", "status": 1}
                """.formatted(newUserId, System.currentTimeMillis() % 10000, System.currentTimeMillis() % 10000);

        mockMvc.perform(post("/user-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.realName").value("测试"));
    }

    @Test
    void update_shouldReturnUpdatedInfo() throws Exception {
        String body = """
                {"realName": "张三_updated", "phone": "13800138000"}
                """;

        mockMvc.perform(put("/user-info/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.realName").value("张三_updated"));
    }

    @Test
    void countByDepartment_shouldReturnStats() throws Exception {
        mockMvc.perform(get("/user-info/stats/department"))
                .andExpect(status().isOk());
    }

    @Test
    void countByPosition_shouldReturnStats() throws Exception {
        mockMvc.perform(get("/user-info/stats/position"))
                .andExpect(status().isOk());
    }
}
