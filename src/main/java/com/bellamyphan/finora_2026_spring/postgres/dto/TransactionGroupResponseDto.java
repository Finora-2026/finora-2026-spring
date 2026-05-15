package com.bellamyphan.finora_2026_spring.postgres.dto;

import com.bellamyphan.finora_2026_spring.postgres.entity.TransactionGroup;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TransactionGroupResponseDto {

    private String id;
    private String reportId;
    private boolean isRepeatable;
    private List<TransactionResponseDto> transactions;

    public static TransactionGroupResponseDto fromEntity(TransactionGroup group) {
        TransactionGroupResponseDto dto = new TransactionGroupResponseDto();
        dto.setId(group.getId());
        dto.setReportId(group.getReport() != null
                ? group.getReport().getId()
                : null
        );
        dto.setRepeatable(group.isRepeatable());
        dto.setTransactions(group.getTransactions()
                .stream()
                .map(TransactionResponseDto::fromEntity)
                .toList()
        );
        return dto;
    }
}
