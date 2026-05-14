package com.bellamyphan.finora_2026_spring.postgres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogInResponseDto {
    private boolean success;
    private String token;
}
