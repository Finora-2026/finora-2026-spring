package com.bellamyphan.finora_2026_spring.repository;

import com.bellamyphan.finora_2026_spring.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, String> {

    boolean existsByCityIgnoreCaseAndStateIgnoreCase(String city, String state);
}
