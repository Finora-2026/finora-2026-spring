package com.bellamyphan.finora_2026_spring.postgres.repository;

import com.bellamyphan.finora_2026_spring.postgres.entity.Report;
import com.bellamyphan.finora_2026_spring.postgres.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {

    Optional<Report> findTopByUserAndIsPostedTrueOrderByMonthDesc(User user);
    Optional<Report> findTopByUserAndIsPostedFalseOrderByMonthDesc(User user);
}
