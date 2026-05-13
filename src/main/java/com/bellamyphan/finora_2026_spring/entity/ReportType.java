package com.bellamyphan.finora_2026_spring.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "report_types")
@Getter
@Setter
@NoArgsConstructor
@IdClass(ReportTypeId.class) // Composite PK class
public class ReportType {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_report_types_report"))
    private Report report;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_type_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_report_types_transaction_type"))
    private TransactionType transactionType;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;
}
