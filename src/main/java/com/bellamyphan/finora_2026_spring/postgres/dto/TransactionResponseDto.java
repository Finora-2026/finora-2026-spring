package com.bellamyphan.finora_2026_spring.postgres.dto;

import com.bellamyphan.finora_2026_spring.postgres.entity.Transaction;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionResponseDto {

    private String id;
    private String transactionGroupId;
    private LocalDateTime transactionDate;
    private BigDecimal amount;
    private String notes;
    private String accountId;
    private String brandId;
    private String locationId;
    private String transactionTypeId;
    private boolean isPosted;

    public static TransactionResponseDto fromEntity(Transaction transaction) {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setId(transaction.getId());
        dto.setTransactionGroupId(transaction.getTransactionGroup().getId());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setAmount(transaction.getAmount());
        dto.setNotes(transaction.getNotes());
        dto.setAccountId(transaction.getAccount().getId());
        dto.setBrandId(transaction.getBrand() != null ? transaction.getBrand().getId() : null);
        dto.setLocationId(transaction.getLocation() != null ? transaction.getLocation().getId() : null);
        dto.setTransactionTypeId(transaction.getTransactionType() != null ?
                transaction.getTransactionType().getId() : null
        );
        dto.setPosted(transaction.isPosted());
        return dto;
    }
}
