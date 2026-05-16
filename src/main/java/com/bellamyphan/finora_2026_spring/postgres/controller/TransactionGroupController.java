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

    @PutMapping
    public ResponseEntity<?> updateTransactionGroup(@Valid @RequestBody TransactionGroupResponseDto dto) {
        // Validate group id
        if (dto.getId() == null || dto.getId().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Group ID must be provided for update"
            ));
        }

        User user = jwtService.getCurrentUser();
        try {
            transactionGroupService.updateTransactionGroup(dto, user);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Transaction group updated successfully"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Failed to update transaction group: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionGroupResponseDto> getTransactionGroup(@PathVariable String id) {
        User user = jwtService.getCurrentUser();
        TransactionGroupResponseDto dto = transactionGroupService.findTransactionGroupByIdAndUser(id, user);
        return ResponseEntity.ok(dto);
    }
}
