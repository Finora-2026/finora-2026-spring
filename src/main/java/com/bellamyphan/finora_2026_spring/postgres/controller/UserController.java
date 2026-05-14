package com.bellamyphan.finora_2026_spring.postgres.controller;

import com.bellamyphan.finora_2026_spring.postgres.constant.RoleEnum;
import com.bellamyphan.finora_2026_spring.postgres.dto.UserCreateRequestDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.UserCreateResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.User;
import com.bellamyphan.finora_2026_spring.postgres.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Create a new user (public API)
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequestDto userRequest) {
        User savedUser = userService.createUser(userRequest, RoleEnum.ROLE_USER);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserCreateResponseDto.fromEntity(savedUser));
    }
}
