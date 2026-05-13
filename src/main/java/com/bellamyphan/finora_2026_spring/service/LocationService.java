package com.bellamyphan.finora_2026_spring.service;

import com.bellamyphan.finora_2026_spring.dto.LocationCreateRequestDto;
import com.bellamyphan.finora_2026_spring.dto.LocationCreateResponseDto;
import com.bellamyphan.finora_2026_spring.entity.Location;
import com.bellamyphan.finora_2026_spring.repository.LocationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class LocationService {

    private final NanoIdService nanoIdService;

    private final LocationRepository locationRepository;

    /**
     * Create a new location, check for duplicates (ignore case)
     */
    @Transactional
    public LocationCreateResponseDto createLocation(@Valid LocationCreateRequestDto request) {
        String cityNormalized = request.getCity().trim();
        String stateNormalized = request.getState().trim();

        // Check for duplicate
        if (locationRepository.existsByCityIgnoreCaseAndStateIgnoreCase(cityNormalized, stateNormalized)) {
            throw new IllegalArgumentException("Location already exists: " + cityNormalized + ", " + stateNormalized);
        }

        // Create and save
        Location location = new Location(cityNormalized, stateNormalized);
        location.setId(nanoIdService.generateUniqueId(locationRepository));
        return LocationCreateResponseDto.fromEntity(locationRepository.save(location));
    }
}
