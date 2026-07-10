package com.bellamyphan.finora_2026_spring.postgres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportTypeSummaryDto {
    private String transactionTypeId;
    private String transactionTypeName;
    private BigDecimal totalAmount;
}
