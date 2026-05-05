package com.bellamyphan.finora_2026_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data // Lombok annotation to generate getters, setters, equals, hashCode, and toString methods
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 60)
    @NotBlank(message = "Email is required")
    private String email;

    @Column(name = "password_hashed", nullable = false, length = 60)
    @NotBlank(message = "Password is required")
    private String passwordHashed;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_users_roles"))
    private Role role;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    // Constructor without id (id can be generated in service layer)
    public User(String name, String email, String passwordHashed, Role role) {
        this.name = name;
        this.email = email;
        this.passwordHashed = passwordHashed;
        this.role = role;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDate.now();
        }
        setEmail(email);
    }

    @PreUpdate
    public void preUpdate() {
        setEmail(email);
    }

    // Override setter to force lowercase
    public void setEmail(String email) {
        this.email = (email != null) ? email.toLowerCase() : null;
    }

}
