package com.bellamyphan.finora_2026_spring.postgres.controller;

import com.bellamyphan.finora_2026_spring.postgres.dto.*;
import com.bellamyphan.finora_2026_spring.postgres.entity.User;
import com.bellamyphan.finora_2026_spring.postgres.service.AccountService;
import com.bellamyphan.finora_2026_spring.postgres.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public ResponseEntity<AccountEditDto> createAccount(@RequestBody @Valid AccountEditDto accountEditDto) {
        if (accountEditDto.getId() != null) {
            throw new IllegalArgumentException("New account must not contain id");
        }
        if (accountEditDto.getClosingDate() != null) {
            throw new IllegalArgumentException("New account cannot have closing date");
        }

        User user = jwtService.getCurrentUser();
        AccountEditDto savedAccountDto = accountService.createAccount(accountEditDto, user);
        return new ResponseEntity<>(savedAccountDto, HttpStatus.CREATED);
    }


    // -----------------------
    // PUT update existing account
    // -----------------------
    @PutMapping
    public ResponseEntity<AccountEditDto> updateAccount(@RequestBody @Valid AccountEditDto accountEditDto) {
        if (accountEditDto.getId() == null) {
            throw new IllegalArgumentException("Existing account must have an id");
        }

        User user = jwtService.getCurrentUser();
        AccountEditDto savedAccountDto = accountService.updateAccount(accountEditDto, user);
        return new ResponseEntity<>(savedAccountDto, HttpStatus.OK);
    }

    @GetMapping("/check-name")
    public ResponseEntity<Boolean> checkAccountNameAvailability(
            @RequestParam String name
    ) {
        User user = jwtService.getCurrentUser();
        boolean exists = accountService.accountNameExists(name, user);
        return ResponseEntity.ok(!exists);
    }

    @GetMapping("/{id}/validate-date")
    public ResponseEntity<AccountDateValidationResponseDto> validateAccountDate(
            @PathVariable String id,
            @RequestParam String dateTime
    ) {
        User user = jwtService.getCurrentUser();
        LocalDateTime parsedDateTime = LocalDateTime.parse(dateTime);
        boolean valid = accountService.softCheckValidDate(parsedDateTime, id, user);
        return ResponseEntity.ok(new AccountDateValidationResponseDto(valid));
    }

    // -----------------------
    // GET an account by id and user token
    // -----------------------
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getAnAccountByIdByUser(@PathVariable String id) {
        User user = jwtService.getCurrentUser();
        AccountResponseDto accountDto = accountService.findAccountDtoByIdAndUser(id, user);
        return ResponseEntity.ok(accountDto);
    }

    // -----------------------
    // GET an account by id and user token, return edit dto to fetch FE
    // -----------------------
    @GetMapping("/edit/{id}")
    public ResponseEntity<AccountEditDto> getAnAccountByIdByUserForEdit(@PathVariable String id) {
        User user = jwtService.getCurrentUser();
        AccountEditDto accountDto = accountService.findAccountEditDtoByIdAndUser(id, user);
        return ResponseEntity.ok(accountDto);
    }

    // -----------------------
    // GET account balances based on input date
    // -----------------------
    @PostMapping("/balance-as-of-date")
    public ResponseEntity<AccountBalanceResponseDto> getAccountBalanceAsOfDate(
            @Valid @RequestBody AccountBalanceRequestDto accountBalanceRequestDto) {
        User user = jwtService.getCurrentUser();
        return ResponseEntity.ok(accountService.findAccountBalanceAsOfDate(accountBalanceRequestDto, user));
    }

    // -----------------------
    // GET last 30 days of daily balances
    // -----------------------
    @GetMapping("/{id}/daily-balance")
    public ResponseEntity<List<AccountDailyBalanceDto>> getDailyBalance(
            @PathVariable String id,
            @RequestParam(defaultValue = "30") int days
    ) {
        User user = jwtService.getCurrentUser();
        return ResponseEntity.ok(
                accountService.calculateLastNDaysBalances(id, user, days)
        );
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

    // -----------------------
    // GET inactive accounts (closingDate != null)
    // -----------------------
    @GetMapping("/inactive")
    public List<AccountResponseDto> getInactiveAccountsByUser() {
        User user = jwtService.getCurrentUser();
        return accountService.findInactiveAccountsByUser(user);
    }
}
