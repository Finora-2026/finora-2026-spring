package com.bellamyphan.finora_2026_spring.postgres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSoftCheckValidationResponseDto {
    private boolean valid;
}
