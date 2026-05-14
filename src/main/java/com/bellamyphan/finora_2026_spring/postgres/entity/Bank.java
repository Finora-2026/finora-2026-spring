package com.bellamyphan.finora_2026_spring.postgres.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "banks")
@Getter
@Setter
@NoArgsConstructor
public class Bank {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    // Constructor without id (id can be generated in service layer)
    public Bank(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @PrePersist
    public void prePersist() {
        normalizeFields();
    }

    @PreUpdate
    public void preUpdate() {
        normalizeFields();
    }

    private void normalizeFields() {
        if (name != null) {
            name = name.trim();
        }

        if (url != null) {
            url = url.trim();
        }
    }
}
