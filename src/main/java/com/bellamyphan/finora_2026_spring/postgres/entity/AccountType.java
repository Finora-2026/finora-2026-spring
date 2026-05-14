package com.bellamyphan.finora_2026_spring.postgres.entity;

import com.bellamyphan.finora_2026_spring.postgres.constant.AccountTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account_types")
@Getter
@Setter
@NoArgsConstructor
public class AccountType {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @Enumerated(EnumType.STRING) // Store enum as string
    @Column(name = "name", nullable = false, length = 15)
    private AccountTypeEnum name;

    // Constructor with only enum (id generated separately)
    public AccountType(AccountTypeEnum name) {
        this.name = name;
    }
}
