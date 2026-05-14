package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.dto.TransactionCreateDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.TransactionGroupCreateDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.*;
import com.bellamyphan.finora_2026_spring.postgres.repository.TransactionGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionGroupService {

    private final NanoIdService nanoIdService;
    private final AccountService accountService;
    private final BrandService brandService;
    private final LocationService locationService;
    private final TransactionTypeService transactionTypeService;
    private final TransactionService transactionService;

    private final TransactionGroupRepository transactionGroupRepository;

    // ============================================================
    // CREATE TRANSACTION GROUP
    // ============================================================
    @Transactional
    public String createTransactionGroup(TransactionGroupCreateDto dto, User user) {
        validateTransactionList(dto);

        // Create and saved the new group
        TransactionGroup group = createAndSaveNewTransactionGroup(user);

        // Create new transactions and linked to transactionGroup
        for (TransactionCreateDto row : dto.getTransactions()) {

            // Get the account entity
            Account account = accountService.findAccountById(row.getAccountId());

            // Fetch brand if provided
            Brand brand = null;
            if (row.getBrandId() != null && !row.getBrandId().isEmpty()) {
                brand = brandService.findBrandById(row.getBrandId());
            }

            // Fetch location if provided
            Location location = null;
            if (row.getLocationId() != null && !row.getLocationId().isEmpty()) {
                location = locationService.findLocationById(row.getLocationId());
            }

            // Fetch transaction type
            TransactionType transactionType = null;
            if (row.getTransactionTypeId() != null && !row.getTransactionTypeId().isEmpty()) {
                transactionType = transactionTypeService.findTransactionTypeById(row.getTransactionTypeId());
            }

            // Create a transaction entity
            Transaction tx = new Transaction();
            // Id will be handled in the transactionService
            tx.setTransactionGroup(group);
            tx.setTransactionDate(row.getTransactionDate());
            tx.setAmount(row.getAmount());
            tx.setNotes(row.getNotes());
            tx.setAccount(account);
            tx.setBrand(brand);
            tx.setLocation(location);
            tx.setTransactionType(transactionType);
            // pending=true by default

            transactionService.createTransactionFromEntity(tx);
        }

        return group.getId();
    }

    private void validateTransactionList(TransactionGroupCreateDto dto) {
        // Ensure at least 1 transaction
        if (dto.getTransactions() == null || dto.getTransactions().isEmpty()) {
            throw new IllegalArgumentException("Cannot create a group without at least 1 transaction");
        }

        // Ensure each transaction has linked account
        for (TransactionCreateDto row : dto.getTransactions()) {
            if (row.getAccountId() == null || row.getAccountId().isEmpty()) {
                throw new IllegalArgumentException("Each transaction must have an account linked");
            }
        }
    }

    private TransactionGroup createAndSaveNewTransactionGroup(User user) {
        TransactionGroup newTransactionGroup = new TransactionGroup();
        newTransactionGroup.setUser(user);
        String newId = nanoIdService.generateUniqueId(transactionGroupRepository);
        newTransactionGroup.setId(newId);
        return transactionGroupRepository.save(newTransactionGroup);
    }
}
