package com.bellamyphan.finora_2026_spring.dto;

import com.bellamyphan.finora_2026_spring.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@AllArgsConstructor
public class UserCreateResponseDto {
    private String name;
    private String email;
    private String role;
    private boolean isActive;
    private boolean isDemo;
    private LocalDateTime createdAt;

    // ==========================
    // Convert Entity → DTO
    // ==========================
    public static UserCreateResponseDto fromEntity(User user) {
        return Optional.ofNullable(user)
                .map(u -> new UserCreateResponseDto(
                        u.getName(),
                        u.getEmail(),
                        u.getRole().getName().toString(),
                        u.isActive(),
                        u.isDemo(),
                        u.getCreatedAt()
                        ))
                .orElse(null);
    }
}
