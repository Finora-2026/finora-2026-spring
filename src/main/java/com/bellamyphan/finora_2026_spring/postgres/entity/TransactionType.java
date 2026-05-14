package com.bellamyphan.finora_2026_spring.postgres.entity;

import com.bellamyphan.finora_2026_spring.postgres.constant.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transaction_types")
@Getter
@Setter
@NoArgsConstructor
public class TransactionType {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @Enumerated(EnumType.STRING) // Store enum as string in DB
    @Column(name = "name", nullable = false, length = 20)
    private TransactionTypeEnum name;
}
