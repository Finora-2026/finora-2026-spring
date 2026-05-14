package com.bellamyphan.finora_2026_spring.postgres.service;

import com.bellamyphan.finora_2026_spring.postgres.dto.LocationCreateRequestDto;
import com.bellamyphan.finora_2026_spring.postgres.dto.LocationCreateResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.entity.Location;
import com.bellamyphan.finora_2026_spring.postgres.repository.LocationRepository;
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

    public Location findLocationById(String locationId) {
        if (locationId == null || locationId.isBlank()) {
            throw new IllegalArgumentException("Location ID cannot be null or blank");
        }

        return locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException(
                        "Location not found with this id: " + locationId
                ));
    }
}
