package com.bellamyphan.finora_2026_spring.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
public class Brand {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "url", columnDefinition = "TEXT")
    private String url;

    // Constructor without id (id can be generated in service layer)
    public Brand(String name, String url) {
        setName(name);
        setUrl(url);
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }
}
