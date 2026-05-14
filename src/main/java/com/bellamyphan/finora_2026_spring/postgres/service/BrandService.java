package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.dto.BrandCreateRequestDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.BrandCreateResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.Brand;
import com.bellamyphan.finora_2026_spring.postgres.repository.BrandRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class BrandService {

    private final NanoIdService nanoIdService;

    private final BrandRepository brandRepository;

    @Transactional
    public BrandCreateResponseDto createBrand(@Valid BrandCreateRequestDto request) {
        // Normalize name and URL
        String normalizedName = request.getName().trim();
        String urlNormalized = request.getUrl() != null ? request.getUrl().trim().toLowerCase() : null;

        // Check for name or url duplication
        if (brandRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Brand name already exists: '" + normalizedName + "'");
        }
        if (urlNormalized != null && !urlNormalized.isBlank() && brandRepository.existsByUrlIgnoreCase(urlNormalized)) {
            throw new IllegalArgumentException("Brand URL already exists: '" + urlNormalized + "'");
        }

        // Create brand and save to DB.
        Brand brand = new Brand(normalizedName, urlNormalized);
        brand.setId(nanoIdService.generateUniqueId(brandRepository));
        return BrandCreateResponseDto.fromEntity(brandRepository.save(brand));
    }
}
