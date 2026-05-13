package com.bellamyphan.finora_2026_spring.dto;

import com.bellamyphan.finora_2026_spring.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class BrandCreateResponseDto {

    private String id;

    private String name;

    private String url;

    // ==========================
    // Convert Entity → DTO
    // ==========================
    public static BrandCreateResponseDto fromEntity(Brand brand) {
        return Optional.ofNullable(brand)
                .map(b -> new BrandCreateResponseDto(
                        b.getId(),
                        b.getName(),
                        b.getUrl()))
                .orElse(null);
    }
}
