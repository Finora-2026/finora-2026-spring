package com.bellamyphan.finora_2026_spring.repository;

import com.bellamyphan.finora_2026_spring.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
