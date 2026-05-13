package com.bellamyphan.finora_2026_spring.repository;

import com.bellamyphan.finora_2026_spring.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {

    // Check if a brand with the same name exists (case-insensitive)
    boolean existsByNameIgnoreCase(String name);

    // Check if a brand with the same URL exists (case-insensitive)
    boolean existsByUrlIgnoreCase(String url);
}
