package com.bellamyphan.finora_2026_spring.postgres.repository;

import com.bellamyphan.finora_2026_spring.postgres.entity.TransactionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionGroupRepository extends JpaRepository<TransactionGroup, String> {

    @Query("""
        SELECT DISTINCT tg
        FROM TransactionGroup tg
        LEFT JOIN FETCH tg.transactions t
        LEFT JOIN FETCH t.account
        LEFT JOIN FETCH t.brand
        LEFT JOIN FETCH t.location
        LEFT JOIN FETCH t.transactionType
        WHERE tg.id = :id
          AND tg.user.id = :userId
    """)
    Optional<TransactionGroup> findByIdAndUserIdWithTransactions(
            String id,
            String userId
    );

    List<TransactionGroup> findAllByReportIdAndUserId(
            String reportId,
            String userId
    );

    @Query("""
        SELECT g
        FROM TransactionGroup g
        JOIN g.transactions tx
        WHERE g.user.id = :userId
          AND g.report IS NULL
          AND NOT EXISTS (
              SELECT 1
              FROM Transaction t
              WHERE t.transactionGroup = g
                AND t.isPosted = false
          )
        GROUP BY g
        ORDER BY MIN(tx.transactionDate) ASC
    """)
    List<TransactionGroup> findPostedAndUnreportedGroupsByUserId(String userId);

    /**
     * Finds the minimum transaction date across all groups that are completely posted and unreported.
     */
    @Query("""
        SELECT MIN(tx.transactionDate)
        FROM TransactionGroup g
        JOIN g.transactions tx
        WHERE g.user.id = :userId
          AND g.report IS NULL
          AND NOT EXISTS (
              SELECT 1
              FROM Transaction t
              WHERE t.transactionGroup = g
                AND t.isPosted = false
          )
    """)
    Optional<LocalDate> findMinTransactionDateForPostedAndUnreported(String userId);

    @Query("""
        SELECT g
        FROM TransactionGroup g
        JOIN g.transactions tx
        WHERE g.user.id = :userId
          AND g.isRepeatable = true
        GROUP BY g
        ORDER BY MIN(tx.transactionDate) ASC
    """)
    List<TransactionGroup> findRepeatableGroupsByUserId(String userId);

    @Modifying
    @Query("""
        UPDATE TransactionGroup g
        SET g.isRepeatable = false
        WHERE g.user.id = :userId
          AND g.isRepeatable = true
    """)
    int markAllRepeatableGroupsNotRepeatableByUserId(String userId);
}
