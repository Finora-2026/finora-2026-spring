package com.bellamyphan.finora_2026_spring.postgres.dto;

import com.bellamyphan.finora_2026_spring.postgres.entity.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEditDto {

    private String id; // null for create, present for update

    @NotBlank(message = "Account name is required")
    private String name;

    @NotNull(message = "Account must have an opening date")
    private LocalDateTime openingDate;

    // Optional
    private LocalDateTime closingDate;

    @NotBlank(message = "Account must belong to a bank group")
    private String bankId;

    @NotBlank(message = "Account must have a type")
    private String typeId;

    public static AccountEditDto fromEntity(Account account) {
        if (account == null) return null;
        return new AccountEditDto(
                account.getId(),
                account.getName(),
                account.getOpeningDate(),
                account.getClosingDate(),
                account.getBank().getId(),
                account.getAccountType().getId()
        );
    }
}
