package com.bellamyphan.finora_2026_spring.postgres.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AccountBalanceRequestDto {

    @NotBlank
    private String accountId;
    @NotNull
    private LocalDateTime asOfDate;
}
