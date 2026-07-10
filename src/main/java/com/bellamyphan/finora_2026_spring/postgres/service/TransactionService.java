package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.dto.TransactionResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.TransactionSearchRequestDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.*;
import com.bellamyphan.finora_2026_spring.postgres.repository.TransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final EntityManager em;

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
        return transactionRepository
                .findByAccount_User_IdAndIsPostedFalseOrderByTransactionDateAsc(user.getId())
                .stream()
                .map(TransactionResponseDto::fromEntity)
                .toList();
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
        // Validate required fields first
        if (txDto.getTransactionDate() == null) {
            throw new IllegalArgumentException("Transaction date is required");
        }

        if (txDto.getAccountId() == null || txDto.getAccountId().isBlank()) {
            throw new IllegalArgumentException("Account is required");
        }

        // Transaction date must be inside account active period
        accountService.validateTransactionDateForAccount(
                txDto.getTransactionDate().toLocalDate(),
                txDto.getAccountId(),
                user
        );

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

    public List<TransactionResponseDto> searchTransactions(TransactionSearchRequestDto searchDto, User user) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> transaction = cq.from(Transaction.class);

        List<Predicate> predicates = new ArrayList<>();

        // --------------------
        // DATE FILTERS
        // --------------------
        if (searchDto.getStartDate() != null) {
            predicates.add(
                    cb.greaterThanOrEqualTo(
                            transaction.get("transactionDate"),
                            searchDto.getStartDate().atStartOfDay()
                    )
            );
        }

        if (searchDto.getEndDate() != null) {
            predicates.add(
                    cb.lessThanOrEqualTo(
                            transaction.get("transactionDate"),
                            searchDto.getEndDate().atTime(23, 59, 59)
                    )
            );
        }

        // --------------------
        // AMOUNT FILTERS
        // --------------------
        if (searchDto.getMinAmount() != null) {
            predicates.add(
                    cb.greaterThanOrEqualTo(transaction.get("amount"), searchDto.getMinAmount())
            );
        }

        if (searchDto.getMaxAmount() != null) {
            predicates.add(
                    cb.lessThanOrEqualTo(transaction.get("amount"), searchDto.getMaxAmount())
            );
        }

        // --------------------
        // BANK FILTER
        // --------------------
        if (StringUtils.hasText(searchDto.getBankId())) {
            predicates.add(
                    cb.equal(transaction.get("account").get("bank").get("id"), searchDto.getBankId())
            );
        }

        // --------------------
        // ACCOUNT FILTER (NEW)
        // --------------------
        if (StringUtils.hasText(searchDto.getAccountId())) {
            predicates.add(
                    cb.equal(transaction.get("account").get("id"), searchDto.getAccountId())
            );
        }

        // --------------------
        // BRAND FILTER
        // --------------------
        if (StringUtils.hasText(searchDto.getBrandId())) {
            predicates.add(
                    cb.equal(transaction.get("brand").get("id"), searchDto.getBrandId())
            );
        }

        // --------------------
        // LOCATION FILTER
        // --------------------
        if (StringUtils.hasText(searchDto.getLocationId())) {
            predicates.add(
                    cb.equal(transaction.get("location").get("id"), searchDto.getLocationId())
            );
        }

        // --------------------
        // TYPE FILTER (IMPORTANT FIX)
        // --------------------
        if (StringUtils.hasText(searchDto.getTypeId())) {
            predicates.add(
                    cb.equal(transaction.get("transactionType").get("id"), searchDto.getTypeId())
            );
        }

        // --------------------
        // REPORT FILTER
        // --------------------
        if (StringUtils.hasText(searchDto.getReportId())) {
            predicates.add(
                    cb.equal(transaction.get("transactionGroup").get("report").get("id"), searchDto.getReportId())
            );
        }

        // --------------------
        // NOTES SEARCH (LIKE)
        // --------------------
        if (StringUtils.hasText(searchDto.getNotes())) {
            predicates.add(
                    cb.like(
                            cb.lower(transaction.get("notes")),
                            "%" + searchDto.getNotes().toLowerCase() + "%"
                    )
            );
        }

        // --------------------
        // USER OWNERSHIP FILTER (IMPORTANT)
        // --------------------
        predicates.add(
                cb.equal(transaction.get("account").get("user").get("id"), user.getId())
        );

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.asc(transaction.get("transactionDate")));

        return em.createQuery(cq)
                .getResultList()
                .stream()
                .map(TransactionResponseDto::fromEntity)
                .toList();
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
