package com.bellamyphan.finora_2026_spring.repository;

import com.bellamyphan.finora_2026_spring.entity.Account;
import com.bellamyphan.finora_2026_spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findByUser(User user);

}
