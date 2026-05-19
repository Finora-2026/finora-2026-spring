package com.bellamyphan.finora_2026_spring.postgres.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", foreignKey = @ForeignKey(name = "fk_transaction_groups_report"))
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_transaction_groups_user")
    )
    private User user;

    @Column(name = "is_repeatable", nullable = false)
    private boolean isRepeatable = false;

    @Column(name = "last_repeated_at")
    private LocalDateTime lastRepeatedAt;

    // Link to transactions
    @OneToMany(mappedBy = "transactionGroup", fetch = FetchType.LAZY)
    private List<Transaction> transactions;
}
