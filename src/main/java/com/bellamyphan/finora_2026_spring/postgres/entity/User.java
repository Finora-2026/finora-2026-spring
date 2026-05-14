package com.bellamyphan.finora_2026_spring.postgres.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "email", nullable = false, length = 60)
    private String email;

    @Column(name = "password_hashed", nullable = false, length = 60)
    private String passwordHashed;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_users_roles"))
    private Role role;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "is_demo", nullable = false)
    private boolean isDemo = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructor without id (id can be generated in service layer)
    public User(String name, String email, String passwordHashed, Role role) {
        this.name = name;
        this.email = email;
        this.passwordHashed = passwordHashed;
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = (email == null)
                ? null
                : email.trim().toLowerCase();
    }
}