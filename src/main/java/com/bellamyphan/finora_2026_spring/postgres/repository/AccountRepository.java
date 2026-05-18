package com.bellamyphan.finora_2026_spring.postgres.repository;

import com.bellamyphan.finora_2026_spring.postgres.entity.Account;
import com.bellamyphan.finora_2026_spring.postgres.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findByUser(User user);

    Optional<Account> findByIdAndUser_Id(String accountId, String userId);

    List<Account> findByUser_IdAndBank_Id(String userId, String bankId);

    boolean existsByUser_IdAndNameIgnoreCase(String userId, String name);

}
