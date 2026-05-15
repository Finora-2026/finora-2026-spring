package com.bellamyphan.finora_2026_spring.postgres.repository;

import com.bellamyphan.finora_2026_spring.postgres.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByAccount_User_IdAndIsPostedFalse(String userId);

}
