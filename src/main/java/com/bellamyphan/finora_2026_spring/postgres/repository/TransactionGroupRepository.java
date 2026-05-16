package com.bellamyphan.finora_2026_spring.postgres.repository;

import com.bellamyphan.finora_2026_spring.postgres.entity.TransactionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    @Query("""
        SELECT DISTINCT g
        FROM TransactionGroup g
        WHERE g.user.id = :userId
          AND g.report IS NULL
          AND NOT EXISTS (
              SELECT 1
              FROM Transaction t
              WHERE t.transactionGroup = g
              AND t.isPosted = false
          )
    """)
    List<TransactionGroup> findPostedAndUnreportedGroupsByUserId(String userId);

//    @Query("""
//        SELECT DISTINCT g
//        FROM TransactionGroup g
//        LEFT JOIN FETCH g.transactions t
//        LEFT JOIN FETCH t.account
//        LEFT JOIN FETCH t.brand
//        LEFT JOIN FETCH t.location
//        LEFT JOIN FETCH t.transactionType
//        WHERE g.user.id = :userId
//          AND g.report IS NULL
//          AND NOT EXISTS (
//              SELECT 1
//              FROM Transaction t2
//              WHERE t2.transactionGroup = g
//              AND t2.isPosted = false
//          )
//    """)
//    List<TransactionGroup> findPostedAndUnreportedGroupsWithFullTransactions(String userId);

}
