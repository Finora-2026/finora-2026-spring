package com.bellamyphan.finora_2026_spring.postgres.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "report_accounts")
@Getter
@Setter
@NoArgsConstructor
@IdClass(ReportAccountId.class) // Composite PK class
public class ReportAccount {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_report_accounts_report"))
    private Report report;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_report_accounts_account"))
    private Account account;

    @Column(name = "balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal balance;
}
