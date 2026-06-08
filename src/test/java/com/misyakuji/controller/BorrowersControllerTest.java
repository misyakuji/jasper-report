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
@WithMockUser
class BorrowersControllerTest {

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
    void getAll_shouldReturnBorrowerList() throws Exception {
        mockMvc.perform(get("/borrowers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))));
    }

    @Test
    void getById_withValidId_shouldReturnBorrower() throws Exception {
        mockMvc.perform(get("/borrowers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowerId").value(1))
                .andExpect(jsonPath("$.name").isString());
    }

    @Test
    void getById_withInvalidId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/borrowers/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByUserId_shouldReturnRelatedBorrowers() throws Exception {
        mockMvc.perform(get("/borrowers/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))));
    }

    @Test
    void getUnlinkedBorrowers_shouldReturnList() throws Exception {
        mockMvc.perform(get("/borrowers/unlinked"))
                .andExpect(status().isOk());
    }

    @Test
    void checkUserLinked_shouldReturnBoolean() throws Exception {
        mockMvc.perform(get("/borrowers/check-linked/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isBoolean());
    }

    @Test
    void create_shouldReturnCreatedBorrower() throws Exception {
        String body = """
                {"name": "测试借款人", "tel": "13800138000"}
                """;

        mockMvc.perform(post("/borrowers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("测试借款人"));
    }

    @Test
    void update_shouldReturnUpdatedBorrower() throws Exception {
        String body = """
                {"name": "王先生_updated"}
                """;

        mockMvc.perform(put("/borrowers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("王先生_updated"));
    }

    @Test
    void patch_shouldPartiallyUpdateBorrower() throws Exception {
        String body = """
                {"tel": "15560222562"}
                """;

        mockMvc.perform(patch("/borrowers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tel").value("15560222562"));
    }

    @Test
    void searchBorrowers_shouldReturnResults() throws Exception {
        mockMvc.perform(get("/borrowers/search").param("userId", "1").param("name", "王"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))));
    }

    @Test
    void getBorrowerWithFullDetails_shouldReturnCompleteData() throws Exception {
        mockMvc.perform(get("/borrowers/1/full-details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowerId").value(1))
                .andExpect(jsonPath("$.borrowerDetails").exists());
    }

    @Test
    void calculator_shouldUpdateFinancialData() throws Exception {
        mockMvc.perform(post("/borrowers/calculator/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLoan").isNumber());
    }

    @Test
    void calculatorAll_shouldUpdateAllFinancialData() throws Exception {
        mockMvc.perform(post("/borrowers/calculator"))
                .andExpect(status().isOk());
    }
}
