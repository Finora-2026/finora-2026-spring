package com.bellamyphan.finora_2026_spring.postgres.repository;

import com.bellamyphan.finora_2026_spring.postgres.constant.TransactionTypeEnum;
import com.bellamyphan.finora_2026_spring.postgres.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionType, String> {

    Optional<TransactionType> findByName(TransactionTypeEnum name);
}
