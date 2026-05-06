package com.bellamyphan.finora_2026_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogInResponseDto {
    private boolean success;
    private String token;
}
