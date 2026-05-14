package com.bellamyphan.finora_2026_spring.postgres.dto;

import lombok.Data;

import java.util.List;

@Data
public class TransactionGroupCreateDto {

    private List<TransactionCreateDto> transactions;
    
}
