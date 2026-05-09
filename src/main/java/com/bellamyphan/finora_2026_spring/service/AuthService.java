package com.bellamyphan.finora_2026_spring.service;

import com.bellamyphan.finora_2026_spring.dto.LogInRequestDto;
import com.bellamyphan.finora_2026_spring.dto.LogInResponseDto;
import com.bellamyphan.finora_2026_spring.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public LogInResponseDto login(LogInRequestDto dto) {

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
}