package com.bellamyphan.finora_2026_spring.postgres.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionCreateDto {

    @NotNull(message = "Transaction must have a date")
    private LocalDateTime transactionDate;

    @NotNull(message = "Transaction must have an amount (positive or negative)")
    private BigDecimal amount;

    private String notes;

    @NotBlank(message = "Transaction must belong to an account")
    private String accountId;

    private String brandId;

    private String locationId;

    private String transactionTypeId;
}
