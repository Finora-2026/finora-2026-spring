package com.bellamyphan.finora_2026_spring.postgres.dto;

import com.bellamyphan.finora_2026_spring.postgres.entity.Report;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ReportDto {

    private String id;
    private LocalDate month;
    private boolean isPosted;

    public ReportDto(Report report) {
        this.id = report.getId();
        this.month = report.getMonth();
        this.isPosted = report.isPosted();
    }
}
