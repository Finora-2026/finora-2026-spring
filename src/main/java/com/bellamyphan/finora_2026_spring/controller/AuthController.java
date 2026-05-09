package com.bellamyphan.finora_2026_spring.controller;

import com.bellamyphan.finora_2026_spring.dto.LogInRequestDto;
import com.bellamyphan.finora_2026_spring.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LogInRequestDto logInRequestDto) {
        try {
            return ResponseEntity.ok(authService.login(logInRequestDto));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }
}
