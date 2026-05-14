package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.entity.*;
import com.bellamyphan.finora_2026_spring.postgres.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final NanoIdService nanoIdService;

    // Transaction group will be transactional and call this method
    public void createTransactionFromEntity (Transaction transaction) {

        String newId = nanoIdService.generateUniqueId(transactionRepository);
        transaction.setId(newId);
        transactionRepository.save(transaction);
    }
}
