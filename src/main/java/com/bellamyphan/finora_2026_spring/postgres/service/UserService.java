package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.constant.RoleEnum;
import com.bellamyphan.finora_2026_spring.postgres.dto.UserCreateRequestDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.Role;
import com.bellamyphan.finora_2026_spring.postgres.entity.User;
import com.bellamyphan.finora_2026_spring.postgres.repository.RoleRepository;
import com.bellamyphan.finora_2026_spring.postgres.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final PasswordService passwordService;
    private final NanoIdService nanoIdService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public User createUser(@Valid UserCreateRequestDto userDto, RoleEnum forcedRole) {

        // 1. Validate email
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        String email = userDto.getEmail().trim().toLowerCase();

        // 2. Check if email already exists
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        // 3. Validate password
        if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // 4. Get the role, default to ROLE_USER
        RoleEnum finalRole = (forcedRole != null)
                ? forcedRole
                : RoleEnum.ROLE_USER;

        // 5. Fetch role entity from DB, default to ROLE_USER if not found
        Role role = roleRepository.findByName(finalRole)
                .orElseThrow(() -> new RuntimeException("Role not found in DB: " +  finalRole.name()));

        // 6. Create User entity
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(email);
        user.setPasswordHashed(passwordService.hash(userDto.getPassword()));
        user.setRole(role);

        // 7. Generate unique NanoID with retry
        String newId = nanoIdService.generateUniqueId(userRepository);
        user.setId(newId);
        return userRepository.save(user);
    }

    @Transactional
    public User createDemoUser() {

        // 1. Generate unique email (no validation needed against input)
        String email = generateDemoEmail();

        // 2. Generate random password (never returned)
        String rawPassword = generateDemoPassword();

        // 3. Get default role (same as normal users)
        Role role = roleRepository.findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found in DB"));

        // 4. Create user
        User user = new User();
        user.setName("Demo User");
        user.setEmail(email);
        user.setPasswordHashed(passwordService.hash(rawPassword));
        user.setRole(role);
        user.setDemo(true);

        // 5. Generate ID
        String id = nanoIdService.generateUniqueId(userRepository);
        user.setId(id);
        return userRepository.save(user);
    }

    /**
     * Delete expired demo users every 48 hours.
     */
    @Scheduled(fixedRateString = "PT48H", initialDelayString = "PT48H")
    @Async
    @Transactional
    public void deleteExpiredDemoUsers() {

        logger.info("Starting demo user cleanup every 48 hours...");

        LocalDateTime cutoff = LocalDateTime.now().minusHours(48);

        int deletedCount = userRepository.deleteExpiredDemoUsers(cutoff);

        logger.info("Deleted {} expired demo users.", deletedCount);
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public List<User> findAllActiveAdmins() {
        return userRepository.findAllByRoleNameAndIsActiveTrue(RoleEnum.ROLE_ADMIN);
    }

    private String generateDemoEmail() {
        return "demo_" + java.util.UUID.randomUUID() + "@finora.local";
    }

    private String generateDemoPassword() {
        return "demo_" + java.util.UUID.randomUUID();
    }
}
