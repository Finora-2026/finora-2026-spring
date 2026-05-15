package com.bellamyphan.finora_2026_spring.postgres.controller;

import com.bellamyphan.finora_2026_spring.postgres.dto.TransactionGroupCreateDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.TransactionGroupResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.User;
import com.bellamyphan.finora_2026_spring.postgres.service.JwtService;
import com.bellamyphan.finora_2026_spring.postgres.service.TransactionGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/transaction-groups")
@RequiredArgsConstructor
public class TransactionGroupController {

    private final TransactionGroupService transactionGroupService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<?> createTransactionGroup(@Valid @RequestBody TransactionGroupCreateDto dto) {
        User user = jwtService.getCurrentUser();
        String groupId = transactionGroupService.createTransactionGroup(dto, user);
        return ResponseEntity.status(201).body(Map.of(
                "success", true,
                "groupId", groupId,
                "message", "Transaction group created successfully"
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionGroupResponseDto> getTransactionGroup(@PathVariable String id) {
        User user = jwtService.getCurrentUser();
        TransactionGroupResponseDto dto = transactionGroupService.findTransactionGroupById(id, user);
        return ResponseEntity.ok(dto);
    }
}
