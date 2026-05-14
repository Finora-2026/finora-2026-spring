package com.bellamyphan.finora_2026_spring.postgres.controller;

import com.bellamyphan.finora_2026_spring.postgres.dto.BrandCreateResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<BrandCreateResponseDto>> getAllBrandsForCurrentUser() {
        List<BrandCreateResponseDto> brands = brandService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(brands);
    }
}
