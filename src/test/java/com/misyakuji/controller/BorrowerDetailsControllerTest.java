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
@WithMockUser
class BorrowerDetailsControllerTest {

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
    void getAll_shouldReturnDetailsList() throws Exception {
        mockMvc.perform(get("/borrowerDetails"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))));
    }

    @Test
    void findByBorrowerId_withValidId_shouldReturnDetails() throws Exception {
        mockMvc.perform(get("/borrowerDetails/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))))
                .andExpect(jsonPath("$[0].transactionType").isString());
    }

    @Test
    void findByBorrowerId_withInvalidId_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/borrowerDetails/99999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    void create_shouldReturnCreatedDetail() throws Exception {
        String body = """
                {"borrower": {"borrowerId": 1}, "transactionType": "LOAN", "amount": 5000, "transactionDate": "2026-06-08", "notes": "测试"}
                """;

        mockMvc.perform(post("/borrowerDetails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.detailId").isNumber())
                .andExpect(jsonPath("$.amount").value(5000));
    }

    @Test
    void createAll_shouldReturnCreatedDetails() throws Exception {
        String body = """
                [
                    {"borrower": {"borrowerId": 1}, "transactionType": "LOAN", "amount": 1000, "transactionDate": "2026-06-08", "notes": "批量测试1"},
                    {"borrower": {"borrowerId": 1}, "transactionType": "REPAYMENT", "amount": -500, "transactionDate": "2026-06-08", "notes": "批量测试2"}
                ]
                """;

        mockMvc.perform(post("/borrowerDetails/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void update_shouldReturnUpdatedDetail() throws Exception {
        String createBody = """
                {"borrower": {"borrowerId": 1}, "transactionType": "LOAN", "amount": 5000, "transactionDate": "2026-06-08", "notes": "测试更新"}
                """;

        String response = mockMvc.perform(post("/borrowerDetails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int newId = com.jayway.jsonpath.JsonPath.read(response, "$.detailId");

        String updateBody = """
                {"borrower": {"borrowerId": 1}, "amount": 9999, "notes": "更新后的备注"}
                """;

        mockMvc.perform(put("/borrowerDetails/" + newId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(9999));

        mockMvc.perform(delete("/borrowerDetails/" + newId))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        String createBody = """
                {"borrower": {"borrowerId": 1}, "transactionType": "LOAN", "amount": 3000, "transactionDate": "2026-06-08", "notes": "待删除"}
                """;

        String response = mockMvc.perform(post("/borrowerDetails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int newId = com.jayway.jsonpath.JsonPath.read(response, "$.detailId");

        mockMvc.perform(delete("/borrowerDetails/" + newId))
                .andExpect(status().isNoContent());
    }
}
