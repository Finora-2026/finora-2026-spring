package com.bellamyphan.finora_2026_spring.repository;

import com.bellamyphan.finora_2026_spring.constant.RoleEnum;
import com.bellamyphan.finora_2026_spring.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    // JpaRepository provides basic CRUD operations

    Optional<Role> findByName(RoleEnum name);
}
