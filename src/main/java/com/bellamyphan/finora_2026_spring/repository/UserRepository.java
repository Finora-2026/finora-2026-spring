package com.bellamyphan.finora_2026_spring.repository;

import com.bellamyphan.finora_2026_spring.constant.RoleEnum;
import com.bellamyphan.finora_2026_spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // JpaRepository provides basic CRUD operations

    // Find user by email (used for login/authentication)
    Optional<User> findByEmailIgnoreCase(String email);

    // Check if a user exists by email
    boolean existsByEmailIgnoreCase(String email);

    // Finds users where the role name matches AND isActive is true
    List<User> findAllByRoleNameAndIsActiveTrue(RoleEnum roleName);
}
