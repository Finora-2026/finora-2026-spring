package com.bellamyphan.finora_2026_spring.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transaction_groups")
@Getter
@Setter
@NoArgsConstructor
public class TransactionGroup {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", foreignKey = @ForeignKey(name = "fk_transaction_groups_report"))
    private Report report;
}
