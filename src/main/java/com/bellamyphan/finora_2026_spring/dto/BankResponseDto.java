package com.bellamyphan.finora_2026_spring.dto;

import com.bellamyphan.finora_2026_spring.entity.Bank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class BankResponseDto {

    String id;
    String name;

    public static BankResponseDto fromEntity(Bank bank) {
        return Optional.ofNullable(bank)
                .map(b -> new BankResponseDto(
                        b.getId(),
                        b.getName())
                )
                .orElse(null);
    }
}
