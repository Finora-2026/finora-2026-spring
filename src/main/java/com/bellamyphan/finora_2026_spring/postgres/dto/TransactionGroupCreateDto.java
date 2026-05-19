package com.bellamyphan.finora_2026_spring.postgres.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TransactionGroupCreateDto {

    @NotEmpty(message = "Transaction group must contain at least one transaction")
    @Valid
    private List<TransactionCreateDto> transactions;

    private String repeatedFromGroupId;
    
}
