package com.bellamyphan.finora_2026_spring.postgres.controller;

import com.bellamyphan.finora_2026_spring.postgres.dto.ReportCreateDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.ReportDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.User;
import com.bellamyphan.finora_2026_spring.postgres.service.JwtService;
import com.bellamyphan.finora_2026_spring.postgres.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/current-pending")
    public ResponseEntity<ReportDto> getCurrentPendingReport() {
        User user = jwtService.getCurrentUser();
        return reportService.getCurrentPendingReport(user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/last-posted")
    public ResponseEntity<ReportDto> getLastPostedReport() {
        User user = jwtService.getCurrentUser();
        return reportService.getLastPostedReport(user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
