package com.bellamyphan.finora_2026_spring.service;

import com.bellamyphan.finora_2026_spring.constant.RoleEnum;
import com.bellamyphan.finora_2026_spring.dto.UserCreateRequestDto;
import com.bellamyphan.finora_2026_spring.entity.Role;
import com.bellamyphan.finora_2026_spring.entity.User;
import com.bellamyphan.finora_2026_spring.repository.RoleRepository;
import com.bellamyphan.finora_2026_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordService passwordService;
    private final NanoIdService nanoIdService;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public User createUser(UserCreateRequestDto userDto, RoleEnum forcedRole) {

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
