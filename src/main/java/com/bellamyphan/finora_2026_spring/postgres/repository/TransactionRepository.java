package com.bellamyphan.finora_2026_spring.postgres.repository;

import com.bellamyphan.finora_2026_spring.postgres.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByAccount_User_IdAndIsPostedFalse(String userId);

    List<Transaction> findByAccount_IdAndAccount_User_Id(String accountId, String userId);

    Optional<Transaction> findByIdAndAccount_User_Id(String id, String userId);

    List<Transaction> findByAccountIdAndTransactionDateBetweenOrderByTransactionDateAsc(
            String accountId, LocalDateTime start, LocalDateTime end);

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.account.id = :accountId
            AND t.account.user.id = :userId
    """)
    BigDecimal calculatePendingBalance(
            @Param("accountId") String accountId,
            @Param("userId") String userId
    );

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.account.id = :accountId
            AND t.account.user.id = :userId
            AND t.transactionDate <= :asOfDate
    """)
    BigDecimal calculatePendingBalanceAsOfDate(
            @Param("accountId") String accountId,
            @Param("userId") String userId,
            @Param("asOfDate") LocalDateTime asOfDate
    );

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.account.id = :accountId
            AND t.account.user.id = :userId
            AND t.isPosted = true
    """)
    BigDecimal calculatePostedBalance(
            @Param("accountId") String accountId,
            @Param("userId") String userId
    );

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.account.id = :accountId
            AND t.account.user.id = :userId
            AND t.isPosted = true
            AND t.transactionDate <= :asOfDate
    """)
    BigDecimal calculatePostedBalanceAsOfDate(
            @Param("accountId") String accountId,
            @Param("userId") String userId,
            @Param("asOfDate") LocalDateTime asOfDate
    );
}
