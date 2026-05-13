package com.bellamyphan.finora_2026_spring.service;

import com.bellamyphan.finora_2026_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_2026_spring.entity.TransactionType;
import com.bellamyphan.finora_2026_spring.repository.TransactionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
