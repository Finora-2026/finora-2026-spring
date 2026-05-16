package com.bellamyphan.finora_2026_spring.postgres.controller;

import com.bellamyphan.finora_2026_spring.postgres.dto.AccountTypeResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.service.AccountTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/account-types")
@RequiredArgsConstructor
public class AccountTypeController {

    private final AccountTypeService accountTypeService;

    @GetMapping
    public ResponseEntity<List<AccountTypeResponseDto>> getAllAccountTypes() {
        List<AccountTypeResponseDto> types = accountTypeService.findAllAccountTypes();
        return ResponseEntity.status(HttpStatus.OK).body(types);
    }
}
