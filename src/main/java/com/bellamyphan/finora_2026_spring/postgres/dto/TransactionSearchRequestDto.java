package com.bellamyphan.finora_2026_spring.postgres.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TransactionSearchRequestDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String bankId;
    private String accountId;
    private String brandId;
    private String locationId;
    private String typeId;
    private String reportId;
    private String notes;
}
