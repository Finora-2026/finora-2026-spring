package com.bellamyphan.finora_2026_spring.postgres.dto;

import com.bellamyphan.finora_2026_spring.postgres.constant.ReportStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ReportDetailsDto {
    private String currentReportId;
    private String previousReportId;
    private String nextReportId;

    private LocalDate month;
    private ReportStatus reportStatus;

    // todo: calculate report type summary
    // todo: calculate report account summary
}
