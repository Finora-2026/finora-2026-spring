package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.dto.TransactionResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.*;
import com.bellamyphan.finora_2026_spring.postgres.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final NanoIdService nanoIdService;

    // @Transactional: Transaction group will be transactional and call this method
    public void createTransactionFromEntity (Transaction transaction) {

        String newId = nanoIdService.generateUniqueId(transactionRepository);
        transaction.setId(newId);
        transactionRepository.save(transaction);
    }

    public List<TransactionResponseDto> getPendingTransactionsForUser(User user) {
        return transactionRepository.findByAccount_User_IdAndIsPostedFalse(user.getId())
                .stream()
                .map(TransactionResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
