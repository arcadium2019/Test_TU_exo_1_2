package org.example.exo13.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exo13.exception.AccountNotFoundException;
import org.example.exo13.exception.InsufficientFundsException;
import org.example.exo13.exception.InvalidAmountException;
import org.example.exo13.model.BankAccount;
import org.example.exo13.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void POST_accounts_creates_account_and_returns_201() throws Exception {
        BankAccount account = new BankAccount("Alice");
        when(service.createAccount("Alice")).thenReturn(account);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of("holder", "Alice"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.holder").value("Alice"))
                .andExpect(jsonPath("$.balance").value(0.0));
    }

    @Test
    void GET_accounts_returns_all_accounts() throws Exception {
        when(service.findAll()).thenReturn(List.of(
                new BankAccount("Alice"),
                new BankAccount("Bob")
        ));

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void GET_accounts_by_number_returns_account() throws Exception {
        BankAccount account = new BankAccount("ACC-001", "Carol");
        when(service.findByNumber("ACC-001")).thenReturn(account);

        mockMvc.perform(get("/accounts/ACC-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holder").value("Carol"));
    }

    @Test
    void GET_accounts_by_unknown_number_returns_404() throws Exception {
        when(service.findByNumber("unknown")).thenThrow(new AccountNotFoundException("Account not found: unknown"));

        mockMvc.perform(get("/accounts/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void POST_deposit_valid_amount_returns_updated_account() throws Exception {
        BankAccount account = new BankAccount("ACC-001", "Dave");
        account.setBalance(150.0);
        when(service.deposit("ACC-001", 50.0)).thenReturn(account);

        mockMvc.perform(post("/accounts/ACC-001/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of("amount", 50.0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150.0));
    }

    @Test
    void POST_deposit_invalid_amount_returns_400() throws Exception {
        when(service.deposit(anyString(), anyDouble()))
                .thenThrow(new InvalidAmountException("Deposit amount must be strictly positive"));

        mockMvc.perform(post("/accounts/ACC-001/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of("amount", 0))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void POST_withdraw_valid_amount_returns_updated_account() throws Exception {
        BankAccount account = new BankAccount("ACC-002", "Eve");
        account.setBalance(120.0);
        when(service.withdraw("ACC-002", 80.0)).thenReturn(account);

        mockMvc.perform(post("/accounts/ACC-002/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of("amount", 80.0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(120.0));
    }

    @Test
    void POST_withdraw_insufficient_funds_returns_422() throws Exception {
        when(service.withdraw(anyString(), anyDouble()))
                .thenThrow(new InsufficientFundsException("Insufficient funds"));

        mockMvc.perform(post("/accounts/ACC-002/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of("amount", 9999))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void POST_transfer_valid_returns_200() throws Exception {
        doNothing().when(service).transfer(anyString(), anyString(), anyDouble());

        mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                Map.of("fromNumber", "ACC-F", "toNumber", "ACC-T", "amount", 100.0))))
                .andExpect(status().isOk());
    }

    @Test
    void POST_transfer_insufficient_funds_returns_422() throws Exception {
        doThrow(new InsufficientFundsException("Insufficient funds for transfer"))
                .when(service).transfer(anyString(), anyString(), anyDouble());

        mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                Map.of("fromNumber", "ACC-F", "toNumber", "ACC-T", "amount", 9999))))
                .andExpect(status().isUnprocessableEntity());
    }
}
