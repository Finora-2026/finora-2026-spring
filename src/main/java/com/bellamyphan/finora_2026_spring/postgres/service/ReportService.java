package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.constant.ReportStatus;
import com.bellamyphan.finora_2026_spring.postgres.dto.ReportCreateDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.ReportAccountSummaryDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.ReportDetailsDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.ReportDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.ReportTypeSummaryDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.Account;
import com.bellamyphan.finora_2026_spring.postgres.entity.Report;
import com.bellamyphan.finora_2026_spring.postgres.entity.Transaction;
import com.bellamyphan.finora_2026_spring.postgres.entity.TransactionGroup;
import com.bellamyphan.finora_2026_spring.postgres.entity.TransactionType;
import com.bellamyphan.finora_2026_spring.postgres.entity.User;
import com.bellamyphan.finora_2026_spring.postgres.repository.AccountRepository;
import com.bellamyphan.finora_2026_spring.postgres.repository.ReportRepository;
import com.bellamyphan.finora_2026_spring.postgres.repository.TransactionGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final NanoIdService nanoIdService;

    private final ReportRepository reportRepository;
    private final TransactionGroupRepository transactionGroupRepository;
    private final AccountRepository accountRepository;

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

    @Transactional(readOnly = true)
    public Optional<ReportDto> getLastPostedReport(User user) {
        return reportRepository
                .findTopByUserAndIsPostedTrueOrderByMonthDesc(user)
                .map(ReportDto::new);
    }

    @Transactional(readOnly = true)
    public Optional<ReportDto> getCurrentPendingReport(User user) {
        return reportRepository
                .findTopByUserAndIsPostedFalseOrderByMonthDesc(user)
                .map(ReportDto::new);
    }

    @Transactional
    public Optional<Integer> loadAllTransactions(String reportId, User user) {
        Optional<Report> reportOptional = reportRepository.findByIdAndUser(reportId, user);
        if (reportOptional.isEmpty()) {
            return Optional.empty();
        }

        Report report = reportOptional.get();
        if (report.isPosted()) {
            throw new IllegalArgumentException("Cannot load transactions into a posted report");
        }

        List<TransactionGroup> availableGroups = transactionGroupRepository
                .findPostedAndUnreportedGroupsByUserId(user.getId());
        for (TransactionGroup group : availableGroups) {
            group.setReport(report);
        }
        transactionGroupRepository.saveAll(availableGroups);

        return Optional.of(availableGroups.size());
    }

    @Transactional(readOnly = true)
    public Optional<ReportDetailsDto> getReportDetails(User user, String reportId) {

        Optional<Report> reportOptional = reportRepository.findByIdAndUser(reportId, user);
        if (reportOptional.isEmpty()) {
            return Optional.empty();
        }

        Report report = reportOptional.get();
        ReportDetailsDto dto = new ReportDetailsDto();
        dto.setCurrentReportId(report.getId());
        dto.setMonth(report.getMonth());
        dto.setReportStatus(
                report.isPosted()
                        ? ReportStatus.POSTED
                        : ReportStatus.PENDING
        );

        // Previous report
        reportRepository
                .findTopByUserAndMonthBeforeOrderByMonthDesc(user, report.getMonth())
                .ifPresent(previous ->
                        dto.setPreviousReportId(previous.getId())
                );

        // Next report
        reportRepository
                .findTopByUserAndMonthAfterOrderByMonthAsc(user, report.getMonth())
                .ifPresent(next ->
                        dto.setNextReportId(next.getId())
                );

        List<TransactionGroup> groups = transactionGroupRepository
                .findAllByReportIdAndUserIdWithTransactions(reportId, user.getId());
        dto.setTypeSummary(calculateTypeSummary(groups));
        dto.setAccountSummary(calculateAccountSummary(user, groups));

        return Optional.of(dto);
    }

    private List<ReportTypeSummaryDto> calculateTypeSummary(List<TransactionGroup> groups) {
        Map<String, ReportTypeSummaryDto> summariesByTypeId = new LinkedHashMap<>();

        for (TransactionGroup group : groups) {
            for (Transaction transaction : group.getTransactions()) {
                TransactionType transactionType = transaction.getTransactionType();
                String transactionTypeId = transactionType != null
                        ? transactionType.getId()
                        : null;
                String transactionTypeName = transactionType != null
                        ? transactionType.getName().name()
                        : null;
                String summaryKey = transactionTypeId != null
                        ? transactionTypeId
                        : "UNCATEGORIZED";

                ReportTypeSummaryDto summary = summariesByTypeId.computeIfAbsent(
                        summaryKey,
                        ignored -> new ReportTypeSummaryDto(
                                transactionTypeId,
                                transactionTypeName,
                                BigDecimal.ZERO
                        )
                );
                summary.setTotalAmount(summary.getTotalAmount().add(transaction.getAmount()));
            }
        }

        return summariesByTypeId.values()
                .stream()
                .sorted(Comparator.comparing(
                        ReportTypeSummaryDto::getTransactionTypeName,
                        Comparator.nullsLast(String::compareTo)
                ))
                .toList();
    }

    private List<ReportAccountSummaryDto> calculateAccountSummary(
            User user,
            List<TransactionGroup> groups
    ) {
        Map<String, ReportAccountSummaryDto> summariesByAccountId = new LinkedHashMap<>();
        List<Account> accounts = accountRepository.findAllByUserIdWithDetails(user.getId())
                .stream()
                .sorted(Comparator
                        .comparing((Account account) -> account.getBank().getName(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Account::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        for (Account account : accounts) {
            summariesByAccountId.put(
                    account.getId(),
                    new ReportAccountSummaryDto(
                            account.getId(),
                            account.getName(),
                            account.getBank().getId(),
                            account.getBank().getName(),
                            account.getAccountType().getName(),
                            BigDecimal.ZERO
                    )
            );
        }

        for (TransactionGroup group : groups) {
            for (Transaction transaction : group.getTransactions()) {
                Account account = transaction.getAccount();
                if (account == null) {
                    continue;
                }

                ReportAccountSummaryDto summary = summariesByAccountId.computeIfAbsent(
                        account.getId(),
                        ignored -> new ReportAccountSummaryDto(
                                account.getId(),
                                account.getName(),
                                account.getBank() != null ? account.getBank().getId() : null,
                                account.getBank() != null ? account.getBank().getName() : null,
                                account.getAccountType() != null ? account.getAccountType().getName() : null,
                                BigDecimal.ZERO
                        )
                );
                summary.setBalance(summary.getBalance().add(transaction.getAmount()));
            }
        }

        return summariesByAccountId.values().stream().toList();
    }
}
