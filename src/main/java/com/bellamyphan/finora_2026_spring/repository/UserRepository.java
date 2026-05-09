package com.bellamyphan.finora_2026_spring.repository;

import com.bellamyphan.finora_2026_spring.constant.RoleEnum;
import com.bellamyphan.finora_2026_spring.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // JpaRepository provides basic CRUD operations

    // Check if a user exists by email
    boolean existsByEmailIgnoreCase(String email);

    // Find user by email (used for login/authentication)
    Optional<User> findByEmailIgnoreCase(String email);

    // Finds users where the role name matches AND isActive is true
    List<User> findAllByRoleNameAndIsActiveTrue(RoleEnum roleName);

    @Modifying
    @Transactional
    @Query("""
    DELETE FROM User u
    WHERE u.isDemo = true
    AND u.createdAt < :cutoff
""")
    int deleteExpiredDemoUsers(@Param("cutoff") LocalDateTime cutoff);
}
