package com.bellamyphan.finora_2026_spring.repository;

import com.bellamyphan.finora_2026_spring.constant.AccountTypeEnum;
import com.bellamyphan.finora_2026_spring.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountTypeRepository extends JpaRepository<AccountType, String> {

    // basic CRUD included

    Optional<AccountType> findByName(AccountTypeEnum name);
}
