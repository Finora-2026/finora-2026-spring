package com.bellamyphan.finora_2026_spring.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "opening_date", nullable = false)
    private LocalDateTime openingDate;

    @Column(name = "closing_date")
    private LocalDateTime closingDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "bank_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_accounts_bank")
    )
    private Bank bank;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "account_type_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_accounts_account_type")
    )
    private AccountType accountType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_accounts_user")
    )
    private User user;

    // Constructor without id
    public Account(
            String name,
            LocalDateTime openingDate,
            LocalDateTime closingDate,
            Bank bank,
            AccountType accountType,
            User user
    ) {
        this.name = name;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.bank = bank;
        this.accountType = accountType;
        this.user = user;
    }

    @PrePersist
    @PreUpdate
    private void normalizeFields() {

        if (name != null) {
            name = name.trim();
        }

        if (openingDate == null) {
            openingDate = LocalDateTime.now();
        }
    }
}
