package com.bellamyphan.finora_2026_spring.postgres.dto;

import com.bellamyphan.finora_2026_spring.postgres.constant.AccountTypeEnum;
import com.bellamyphan.finora_2026_spring.postgres.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {

    private String id;
    private String name;
    private String bankId;
    private String bankName;
    private AccountTypeEnum type;
    private String email;
    private LocalDateTime openingDate;
    private LocalDateTime closingDate;
    private BigDecimal pendingBalance;
    private BigDecimal postedBalance;

    public static AccountResponseDto fromEntity(Account account) {
        if (account == null) return null;

        return new AccountResponseDto(
                account.getId(),
                account.getName(),
                account.getBank() != null ? account.getBank().getId() : null,
                account.getBank() != null ? account.getBank().getName() : null,
                account.getAccountType() != null ? account.getAccountType().getName() : null,
                account.getUser() != null ? account.getUser().getEmail() : null,
                account.getOpeningDate(),
                account.getClosingDate(),
                null, // pendingBalance, will be calculated from transactions
                null  // postedBalance, will be calculated from transactions
        );
    }

    public static AccountResponseDto fromEntity(Account account, BigDecimal pendingBalance, BigDecimal postedBalance) {
        if (account == null) return null;

        return new AccountResponseDto(
                account.getId(),
                account.getName(),
                account.getBank() != null ? account.getBank().getId() : null,
                account.getBank() != null ? account.getBank().getName() : null,
                account.getAccountType() != null ? account.getAccountType().getName() : null,
                account.getUser() != null ? account.getUser().getEmail() : null,
                account.getOpeningDate(),
                account.getClosingDate(),
                pendingBalance,
                postedBalance
        );
    }
}
