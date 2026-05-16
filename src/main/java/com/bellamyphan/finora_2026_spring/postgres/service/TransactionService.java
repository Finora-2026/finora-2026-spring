package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.dto.TransactionResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.*;
import com.bellamyphan.finora_2026_spring.postgres.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final NanoIdService nanoIdService;
    private final AccountService accountService;
    private final BrandService brandService;
    private final LocationService locationService;
    private final TransactionTypeService transactionTypeService;

    // @Transactional: Transaction group will be transactional and call this method
    // Because transactions must be created with a group.
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

    @Transactional
    public void deleteTransaction(String transactionId, User user) {
        // Load transaction and check ownership
        Transaction transaction = transactionRepository
                .findByIdAndAccount_User_Id(transactionId, user.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Transaction not found")
                );
        // Prevent deleting posted transactions
        if (transaction.isPosted()) {
            throw new IllegalArgumentException(
                    "Posted transactions cannot be deleted for this transaction id: " +  transactionId
            );
        }
        transactionRepository.delete(transaction);
    }

    @Transactional
    public void updateTransaction(String transactionId, TransactionResponseDto txDto, User user) {
        // Load transaction and check ownership
        Transaction transaction = transactionRepository
                .findByIdAndAccount_User_Id(transactionId, user.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Transaction not found")
                );
        // Prevent updating posted transactions or just skip
        if (transaction.isPosted()) {
            return;
        }
        // Update all fields except posted
        applyUpdates(transaction, txDto, user);
        // If user requests posting, validate FINAL STATE
        if (txDto.isPosted()) {
            validateForPosting(transaction);
            transaction.setPosted(true);
        }
        transactionRepository.save(transaction);
    }

    // Update all fields except posted
    public void applyUpdates(Transaction transaction, TransactionResponseDto txDto, User user) {
        transaction.setTransactionDate(txDto.getTransactionDate());
        transaction.setAmount(txDto.getAmount());
        transaction.setNotes(txDto.getNotes());
        transaction.setAccount(accountService.findAccountEntityByIdAndUser(txDto.getAccountId(), user));
        if (txDto.getBrandId() != null) {
            transaction.setBrand(
                    brandService.findBrandById(txDto.getBrandId())
            );
        } else {
            transaction.setBrand(null);
        }
        if (txDto.getLocationId() != null) {
            transaction.setLocation(
                    locationService.findLocationById(txDto.getLocationId())
            );
        } else {
            transaction.setLocation(null);
        }
        if (txDto.getTransactionTypeId() != null) {
            transaction.setTransactionType(
                    transactionTypeService.findTransactionTypeById(txDto.getTransactionTypeId())
            );
        } else {
            transaction.setTransactionType(null);
        }
    }

    // If user requests posting, validate FINAL STATE
    private void validateForPosting(Transaction transaction) {
        if (transaction.getTransactionGroup() == null) {
            throw new IllegalArgumentException("Transaction group is required");
        }
        if (transaction.getTransactionDate() == null) {
            throw new IllegalArgumentException("Transaction date is required");
        }
        if (transaction.getAmount() == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        if (transaction.getAccount() == null) {
            throw new IllegalArgumentException("Account is required");
        }
    }
}
