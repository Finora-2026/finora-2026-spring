package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.constant.TransactionTypeEnum;
import com.bellamyphan.finora_2026_spring.postgres.dto.TransactionTypeDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.TransactionType;
import com.bellamyphan.finora_2026_spring.postgres.repository.TransactionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionTypeService {

    private final NanoIdService nanoIdService;

    private final TransactionTypeRepository transactionTypeRepository;

    public boolean existsByType(TransactionTypeEnum typeEnum) {
        return transactionTypeRepository.findByName(typeEnum).isPresent();
    }

    @Transactional
    public TransactionType save(TransactionType type) {
        String newId = nanoIdService.generateUniqueId(transactionTypeRepository);
        type.setId(newId);
        return transactionTypeRepository.save(type);
    }

    public TransactionType findTransactionTypeById(String typeId) {
        if (typeId == null || typeId.isBlank()) {
            throw new IllegalArgumentException("TransactionType ID cannot be null or blank");
        }

        return transactionTypeRepository.findById(typeId)
                .orElseThrow(() -> new RuntimeException(
                        "TransactionType not found with this id: " + typeId
                ));
    }

    public List<TransactionTypeDto> findAll() {
        return transactionTypeRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private TransactionTypeDto toDto(TransactionType type) {
        TransactionTypeDto dto = new TransactionTypeDto();
        dto.setId(type.getId());
        dto.setName(type.getName().name());
        return dto;
    }
}
