package com.bellamyphan.finora_2026_spring.dto;

import com.bellamyphan.finora_2026_spring.entity.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class AccountTypeResponseDto {

    String id;
    String name;

    public static AccountTypeResponseDto fromEntity(AccountType type) {
        return Optional.ofNullable(type)
                .map(t -> new AccountTypeResponseDto(
                        t.getId(),
                        t.getName().name())
                )
                .orElse(null);
    }
}
