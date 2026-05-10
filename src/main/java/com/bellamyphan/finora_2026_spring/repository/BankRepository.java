package com.bellamyphan.finora_2026_spring.repository;

import com.bellamyphan.finora_2026_spring.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, String> {

    // basic CRUD included

    boolean existsByNameIgnoreCase(String name);
}
