package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.dto.TransactionCreateDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.TransactionGroupCreateDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.TransactionGroupResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.TransactionResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.*;
import com.bellamyphan.finora_2026_spring.postgres.repository.TransactionGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
            Account account = accountService.findAccountByIdAndUser(row.getAccountId(), user);

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

    // ============================================================
    // LOAD GROUP BY ID
    // ============================================================
    @Transactional(readOnly = true)
    public TransactionGroupResponseDto findTransactionGroupByIdAndUser(String id, User user) {
        TransactionGroup group = transactionGroupRepository
                .findByIdAndUserIdWithTransactions(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transaction group not found with group id " + id + " and user id " + user.getId())
                );
        return TransactionGroupResponseDto.fromEntity(group);
    }

    @Transactional(readOnly = true)
    public List<TransactionGroupResponseDto> findAvailableReportGroups(User user) {
        List<TransactionGroup> groups =
                transactionGroupRepository.findPostedAndUnreportedGroupsByUserId(user.getId());
        return groups.stream()
                .map(TransactionGroupResponseDto::fromEntity)
                .toList();
    }

    // ============================================================
    // UPDATE GROUP
    // ============================================================
    @Transactional
    public void updateTransactionGroup(TransactionGroupResponseDto dto, User user) {
        // Load transaction group and verify ownership
        TransactionGroup group = transactionGroupRepository
                .findByIdAndUserIdWithTransactions(dto.getId(), user.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transaction group not found with group id " + dto.getId() + " and user id " + user.getId())
                );

        // Cannot delete/update group if linked to report
        if (group.getReport() != null) {
            throw new IllegalArgumentException(
                    "Cannot update transaction group because it has already been included in a report"
            );
        }

        // Load transactions from DB
        List<Transaction> existing = group.getTransactions();
        Map<String, Transaction> existingMap = existing.stream()
                .collect(Collectors.toMap(Transaction::getId, t -> t));
        Set<String> processedIds = new HashSet<>();

        // If groupGto has no transactions, then delete this group and all transaction belong to this group.
        if (dto.getTransactions() == null || dto.getTransactions().isEmpty()) {
            for (Transaction tx : existing) {
                transactionService.deleteTransaction(tx.getId(), user);
            }
            transactionGroupRepository.delete(group);
            return;
        }

        // CREATE + UPDATE
        for (TransactionResponseDto txDto : dto.getTransactions()) {
            if (txDto.getId() == null || !existingMap.containsKey(txDto.getId())) {
                // CREATE NEW TRANSACTION
                Transaction newTx = new Transaction();
                newTx.setTransactionGroup(group);
                transactionService.applyUpdates(newTx, txDto, user);
                transactionService.createTransactionFromEntity(newTx);
            } else {
                // UPDATE EXISTING
                Transaction existingTx = existingMap.get(txDto.getId());
                transactionService.updateTransaction(
                        existingTx.getId(),
                        txDto,
                        user
                );
                processedIds.add(existingTx.getId());
            }
        }

        // DELETE REMOVED TRANSACTIONS
        for (Transaction tx : existing) {
            if (!processedIds.contains(tx.getId())) {
                transactionService.deleteTransaction(tx.getId(), user);
            }
        }
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
