package com.bellamyphan.finora_2026_spring.dto;

import com.bellamyphan.finora_2026_spring.entity.Role;
import com.bellamyphan.finora_2026_spring.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Optional;

@Data
@AllArgsConstructor
public class UserCreateResponseDto {
    private String name;
    private String email;
    private String role;
    private boolean isActive;
    private LocalDate createdAt;

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
                        u.getCreatedAt()
                        ))
                .orElse(null);
    }
}
