package com.bellamyphan.finora_2026_spring.postgres.repository;

import com.bellamyphan.finora_2026_spring.postgres.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {
}
