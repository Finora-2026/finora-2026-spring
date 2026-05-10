package com.bellamyphan.finora_2026_spring.dto;

import com.bellamyphan.finora_2026_spring.constant.AccountTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
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

    @NotNull(message = "Account must have a type")
    private AccountTypeEnum type;
}
