package com.bellamyphan.finora_2026_spring.service;

import com.bellamyphan.finora_2026_spring.dto.LogInRequestDto;
import com.bellamyphan.finora_2026_spring.dto.LogInResponseDto;
import com.bellamyphan.finora_2026_spring.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class AuthService {

    private final UserService userService;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public LogInResponseDto login(@Valid LogInRequestDto dto) {

        User user = userService.findByEmail(
                dto.getEmail().trim().toLowerCase()
        ).orElseThrow(() ->
                new IllegalArgumentException("Invalid email or password")
        );

        // Use PasswordService to check hashed password
        if (!passwordService.matches(
                dto.getPassword(),
                user.getPasswordHashed()
        )) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Generate JWT token with userId, email, role
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().getName() // RoleEnum
        );

        return new LogInResponseDto(true, token);
    }

    public LogInResponseDto loginDemo() {

        User demoUser = userService.createDemoUser();

        // Generate JWT immediately
        String token = jwtService.generateToken(
                demoUser.getEmail(),
                demoUser.getId(),
                demoUser.getRole().getName()
        );

        return new LogInResponseDto(true, token);
    }
}