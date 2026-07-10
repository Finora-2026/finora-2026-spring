package com.bellamyphan.finora_2026_spring.postgres.controller;

import com.bellamyphan.finora_2026_spring.postgres.dto.ReportCreateDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.ReportDetailsDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.ReportDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.User;
import com.bellamyphan.finora_2026_spring.postgres.service.JwtService;
import com.bellamyphan.finora_2026_spring.postgres.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final JwtService jwtService;

    @PostMapping("/create")
    public ResponseEntity<ReportCreateDto> getOrCreatePendingReport() {
        User user = jwtService.getCurrentUser();
        ReportCreateDto response = reportService.createNewReport(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDetailsDto> getReportDetails(
            @PathVariable String reportId) {
        User user = jwtService.getCurrentUser();
        return reportService.getReportDetails(user, reportId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/current-pending")
    public ResponseEntity<ReportDto> getCurrentPendingReport() {
        User user = jwtService.getCurrentUser();
        return reportService.getCurrentPendingReport(user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{reportId}/load-all-transactions")
    public ResponseEntity<?> loadAllTransactions(@PathVariable String reportId) {
        User user = jwtService.getCurrentUser();
        try {
            return reportService.loadAllTransactions(reportId, user)
                    .<ResponseEntity<?>>map(loadedGroupCount -> ResponseEntity.ok(Map.of(
                            "message", "Transactions loaded into report",
                            "loadedGroupCount", loadedGroupCount
                    )))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/last-posted")
    public ResponseEntity<ReportDto> getLastPostedReport() {
        User user = jwtService.getCurrentUser();
        return reportService.getLastPostedReport(user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
