package com.bellamyphan.finora_2026_spring.postgres.dto;

import com.bellamyphan.finora_2026_spring.postgres.constant.AccountTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportAccountSummaryDto {
    private String accountId;
    private String accountName;
    private String bankId;
    private String bankName;
    private AccountTypeEnum accountType;
    private BigDecimal balance;
}
