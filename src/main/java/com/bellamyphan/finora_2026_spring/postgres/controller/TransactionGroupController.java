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

    @PostMapping("/repeat-all")
    public ResponseEntity<?> repeatAllRepeatableGroups() {
        User user = jwtService.getCurrentUser();
        try {
            transactionGroupService.repeatAllRepeatableGroups(user);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Repeatable transaction groups processed successfully"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Failed to repeat transaction groups: " + e.getMessage()
            ));
        }
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

    @PutMapping("/{groupId}/repeatable")
    public ResponseEntity<?> setTransactionGroupRepeatable(
            @PathVariable String groupId, @RequestParam boolean repeatable) {
        User user = jwtService.getCurrentUser();
        try {
            transactionGroupService.setTransactionGroupRepeatable(groupId, repeatable, user);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Transaction group repeatable flag updated successfully"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Failed to update repeatable flag: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/repeatable/disable-all")
    public ResponseEntity<?> disableAllRepeatableGroups() {
        User user = jwtService.getCurrentUser();
        try {
            int updatedCount = transactionGroupService.markAllRepeatableGroupsNotRepeatable(user);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "updatedCount", updatedCount,
                    "message", "All repeatable transaction groups marked as not repeatable"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Failed to disable repeatable transaction groups: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionGroupResponseDto> getTransactionGroup(@PathVariable String id) {
        User user = jwtService.getCurrentUser();
        TransactionGroupResponseDto dto = transactionGroupService.findTransactionGroupByIdAndUser(id, user);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/report/{reportId}")
    public ResponseEntity<?> getTransactionGroupsByReport(
            @PathVariable String reportId) {
        User user = jwtService.getCurrentUser();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", transactionGroupService.findGroupsByReportId(reportId, user)
        ));
    }

    @GetMapping("/available-report-groups")
    public ResponseEntity<?> getAvailableReportGroups() {
        User user = jwtService.getCurrentUser();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", transactionGroupService.findAvailableReportGroups(user)
        ));
    }

    @GetMapping("/repeatable")
    public ResponseEntity<?> getRepeatableGroups() {
        User user = jwtService.getCurrentUser();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", transactionGroupService.findRepeatableGroups(user)
        ));
    }
}
