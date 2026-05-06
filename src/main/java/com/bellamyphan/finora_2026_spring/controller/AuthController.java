package com.bellamyphan.finora_2026_spring.controller;

import com.bellamyphan.finora_2026_spring.dto.LogInRequestDto;
import com.bellamyphan.finora_2026_spring.dto.LogInResponseDto;
import com.bellamyphan.finora_2026_spring.entity.User;
import com.bellamyphan.finora_2026_spring.service.JwtService;
import com.bellamyphan.finora_2026_spring.service.PasswordService;
import com.bellamyphan.finora_2026_spring.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LogInRequestDto logInRequestDto) {
        Optional<User> userOpt = userService.findByEmail(logInRequestDto.getEmail().trim().toLowerCase());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        User user = userOpt.get();

        // Use PasswordService to check hashed password
        if (!passwordService.matches(logInRequestDto.getPassword(), user.getPasswordHashed())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        // Generate JWT token with userId, email, role
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().getName() // RoleEnum
        );

        return ResponseEntity.ok(new LogInResponseDto(true, token));
    }
}
