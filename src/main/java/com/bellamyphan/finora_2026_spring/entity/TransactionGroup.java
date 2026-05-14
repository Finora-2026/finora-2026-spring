package com.bellamyphan.finora_2026_spring.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "transaction_groups")
@Getter
@Setter
@NoArgsConstructor
public class TransactionGroup {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_groups_report"))
    private Report report;

    // Link to transactions
    @OneToMany(mappedBy = "transactionGroup", fetch = FetchType.LAZY)
    private List<Transaction> transactions;
}
