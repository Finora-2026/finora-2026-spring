package com.bellamyphan.finora_2026_spring.postgres.controller;

import com.bellamyphan.finora_2026_spring.postgres.dto.TransactionResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.User;
import com.bellamyphan.finora_2026_spring.postgres.service.JwtService;
import com.bellamyphan.finora_2026_spring.postgres.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final JwtService jwtService;
    private final TransactionService transactionService;

    /**
     * Get all pending transactions for current user
     */
    @GetMapping("/pending")
    public ResponseEntity<List<TransactionResponseDto>> getPendingTransactions() {
        User user = jwtService.getCurrentUser();
        List<TransactionResponseDto> pendingTxs = transactionService.getPendingTransactionsForUser(user);
        return ResponseEntity.ok(pendingTxs);
    }
}
