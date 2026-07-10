package com.bellamyphan.finora_2026_spring.postgres.dto;

import com.bellamyphan.finora_2026_spring.postgres.constant.ReportStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class ReportDetailsDto {
    private String currentReportId;
    private String previousReportId;
    private String nextReportId;

    private LocalDate month;
    private ReportStatus reportStatus;

    private List<ReportTypeSummaryDto> typeSummary = List.of();
    private List<ReportAccountSummaryDto> accountSummary = List.of();

}
