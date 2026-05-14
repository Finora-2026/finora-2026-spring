package com.bellamyphan.finora_2026_spring.postgres.controller;

import com.bellamyphan.finora_2026_spring.postgres.dto.TransactionTypeDto;
import com.bellamyphan.finora_2026_spring.postgres.service.TransactionTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transaction-types")
@RequiredArgsConstructor
public class TransactionTypeController {

    private final TransactionTypeService transactionTypeService;

    @GetMapping
    public ResponseEntity<List<TransactionTypeDto>> getAllTransactionTypesForUser() {
        List<TransactionTypeDto> types = transactionTypeService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(types);
    }
}
