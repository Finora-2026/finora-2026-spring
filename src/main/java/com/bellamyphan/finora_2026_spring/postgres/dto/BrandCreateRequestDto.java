package com.bellamyphan.finora_2026_spring.postgres.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BrandCreateRequestDto {

    @NotBlank(message = "Brand name is required")
    private String name;

    private String url;
}
