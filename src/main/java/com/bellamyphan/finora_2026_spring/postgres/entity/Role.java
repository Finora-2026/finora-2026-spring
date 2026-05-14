package com.bellamyphan.finora_2026_spring.postgres.entity;

import com.bellamyphan.finora_2026_spring.postgres.constant.RoleEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @Enumerated(EnumType.STRING) // Store enum as string in DB
    @Column(name = "name", nullable = false, length = 50)
    private RoleEnum name;

}
