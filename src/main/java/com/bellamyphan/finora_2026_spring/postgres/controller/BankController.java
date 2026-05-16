package com.bellamyphan.finora_2026_spring.postgres.controller;

import com.bellamyphan.finora_2026_spring.postgres.dto.BankResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @GetMapping
    public ResponseEntity<List<BankResponseDto>> getAllBanksForCurrentUser() {
        List<BankResponseDto> banks = bankService.findAllBanks();
        return ResponseEntity.status(HttpStatus.OK).body(banks);
    }
}
