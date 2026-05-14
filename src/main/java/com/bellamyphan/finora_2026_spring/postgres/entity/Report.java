package com.bellamyphan.finora_2026_spring.postgres.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(
        name = "reports",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "reports_user_month_unique",
                        columnNames = {"user_id", "month"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Report {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    /**
     * First day of reporting month.
     * Example: 2026-05-01 = May 2026 report
     */
    @Column(name = "month", nullable = false)
    private LocalDate month; // First day of the month

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reports_user"))
    private User user;

    @Column(name = "is_posted", nullable = false)
    private boolean isPosted = false;

    // Force the first day of the month only
    public void setMonth(LocalDate month) {
        this.month = Objects.requireNonNull(month, "Month cannot be null")
                .withDayOfMonth(1);
    }
}
