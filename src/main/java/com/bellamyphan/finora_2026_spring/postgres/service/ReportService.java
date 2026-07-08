package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.dto.ReportDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.User;
import com.bellamyphan.finora_2026_spring.postgres.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public Optional<ReportDto> getLastPostedReport(User user) {
        return reportRepository
                .findTopByUserAndIsPostedTrueOrderByMonthDesc(user)
                .map(ReportDto::new);
    }

    public Optional<ReportDto> getCurrentPendingReport(User user) {
        return reportRepository
                .findTopByUserAndIsPostedFalseOrderByMonthDesc(user)
                .map(ReportDto::new);
    }
}
