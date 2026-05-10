package com.bellamyphan.finora_2026_spring.controller;

import com.bellamyphan.finora_2026_spring.dto.AccountEditDto;
import com.bellamyphan.finora_2026_spring.dto.AccountResponseDto;
import com.bellamyphan.finora_2026_spring.entity.Account;
import com.bellamyphan.finora_2026_spring.entity.User;
import com.bellamyphan.finora_2026_spring.service.AccountService;
import com.bellamyphan.finora_2026_spring.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final JwtService jwtService;
    private final AccountService accountService;

    // -----------------------
    // POST create a new account
    // -----------------------
    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(@RequestBody @Valid AccountEditDto accountEditDto) {

        // Create, not update
        if (accountEditDto.getId() != null) {
            throw new IllegalArgumentException("New account must not contain id");
        }

        // Create, not closing account
        if (accountEditDto.getClosingDate() != null) {
            throw new IllegalArgumentException("New account cannot have closing date");
        }

        User user = jwtService.getCurrentUser();
        Account savedAccount = accountService.createAccount(accountEditDto, user);

        return new ResponseEntity<>(AccountResponseDto.fromEntity(savedAccount), HttpStatus.CREATED);
    }

    // -----------------------
    // GET all accounts by user token
    // -----------------------
    @GetMapping
    public List<AccountResponseDto> getAllAccountsByUser() {
        User user = jwtService.getCurrentUser();
        return accountService.findAllAccountByUser(user);
    }

    // -----------------------
    // GET active accounts (closingDate == null)
    // -----------------------
    @GetMapping("/active")
    public List<AccountResponseDto> getActiveAccountsByUser() {
        User user = jwtService.getCurrentUser();
        return accountService.findActiveAccountsByUser(user);
    }
}
