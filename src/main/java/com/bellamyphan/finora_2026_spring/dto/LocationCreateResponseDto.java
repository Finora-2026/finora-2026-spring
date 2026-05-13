package com.bellamyphan.finora_2026_spring.dto;

import com.bellamyphan.finora_2026_spring.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class LocationCreateResponseDto {

    private String id;
    private String city;
    private String state;

    // ==========================
    // Convert Entity → DTO
    // ==========================
    public static LocationCreateResponseDto fromEntity(Location location) {
        return Optional.ofNullable(location)
                .map(l -> new LocationCreateResponseDto(
                        l.getId(),
                        l.getCity(),
                        l.getState()))
                .orElse(null);
    }
}
