package com.bellamyphan.finora_2026_spring.postgres.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AccountBalanceResponseDto {

    private String accountId;
    private LocalDateTime asOfDate;
    private BigDecimal pendingBalance;
    private BigDecimal postedBalance;

    public static AccountBalanceResponseDto fromRequestDto(
            AccountBalanceRequestDto dto, BigDecimal pendingBalance, BigDecimal postedBalance) {
        AccountBalanceResponseDto accountBalanceResponseDto = new AccountBalanceResponseDto();
        accountBalanceResponseDto.setAccountId(dto.getAccountId());
        accountBalanceResponseDto.setAsOfDate(dto.getAsOfDate());
        accountBalanceResponseDto.setPendingBalance(pendingBalance);
        accountBalanceResponseDto.setPostedBalance(postedBalance);
        return accountBalanceResponseDto;
    }
}
