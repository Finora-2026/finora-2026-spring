package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.constant.ReportStatus;
import com.bellamyphan.finora_2026_spring.postgres.dto.ReportCreateDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.ReportDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.Report;
import com.bellamyphan.finora_2026_spring.postgres.entity.TransactionGroup;
import com.bellamyphan.finora_2026_spring.postgres.entity.User;
import com.bellamyphan.finora_2026_spring.postgres.repository.ReportRepository;
import com.bellamyphan.finora_2026_spring.postgres.repository.TransactionGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final NanoIdService nanoIdService;

    private final ReportRepository reportRepository;
    private final TransactionGroupRepository transactionGroupRepository;

    @Transactional
    public ReportCreateDto createNewReport(User user) {

        // Find existing pending report, then just return the current pending report.
        Optional<Report> pendingReport = reportRepository.findTopByUserAndIsPostedFalseOrderByMonthDesc(user);
        if (pendingReport.isPresent()) {
            ReportCreateDto dto = new ReportCreateDto();
            dto.setId(String.valueOf(pendingReport.get().getId()));
            dto.setStatus(ReportStatus.PENDING);
            return dto;
        }

        // Find latest posted report, no pending report exists here.
        Optional<Report> latestPostedReport = reportRepository.findTopByUserAndIsPostedTrueOrderByMonthDesc(user);

        // Determine report month based on lastest report if exists, or posted transaction date.
        LocalDate nextMonth;
        if (latestPostedReport.isPresent()) {
            // nextMonth = latest.month + 1 month
            nextMonth = latestPostedReport.get().getMonth().plusMonths(1);
        } else {
            // Find the earliest date among groups that are POSTED and UNREPORTED
            Optional<LocalDate> earliestGroupDate = transactionGroupRepository
                    .findMinTransactionDateForPostedAndUnreported(user.getId());
            // If no eligible transaction groups exist, return EMPTY state cleanly
            // User have no report, user has no posted transaction
            // Don't allow to create new report here.
            if (earliestGroupDate.isEmpty()) {
                ReportCreateDto dto = new ReportCreateDto();
                dto.setId(null);
                dto.setStatus(ReportStatus.EMPTY);
                return dto;
            }
            // nextMonth = first day of the earliest transaction group month
            nextMonth = earliestGroupDate.get().withDayOfMonth(1);
        }

        // Create pending report for nextMonth.
        Report newReport = new Report();
        String newReportId = nanoIdService.generateUniqueId(reportRepository);
        newReport.setId(newReportId);
        newReport.setUser(user);
        newReport.setMonth(nextMonth);
        Report savedReport = reportRepository.save(newReport);

        // Find all posted, unreported groups then add to this new report
        List<TransactionGroup> availableGroups = transactionGroupRepository
                .findPostedAndUnreportedGroupsByUserId(user.getId());
        if (!availableGroups.isEmpty()) {
            for (TransactionGroup group : availableGroups) {
                group.setReport(savedReport);
            }
            transactionGroupRepository.saveAll(availableGroups);
        }

        // Return the new created report, but marked it as empty if groups are empty
        ReportCreateDto dto = new ReportCreateDto();
        dto.setId(String.valueOf(savedReport.getId()));
        if (availableGroups.isEmpty()) {
            dto.setStatus(ReportStatus.EMPTY);
        } else {
            dto.setStatus(ReportStatus.NEW);
        }
        return dto;
    }

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
