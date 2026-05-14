package com.bellamyphan.finora_2026_spring.postgres.controller;

import com.bellamyphan.finora_2026_spring.postgres.dto.LocationCreateResponseDto;
import com.bellamyphan.finora_2026_spring.postgres.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<List<LocationCreateResponseDto>> getAllLocationsForUser() {
        List<LocationCreateResponseDto> locations = locationService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(locations);
    }
}
