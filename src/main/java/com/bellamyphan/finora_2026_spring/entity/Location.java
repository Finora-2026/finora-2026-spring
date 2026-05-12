package com.bellamyphan.finora_2026_spring.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
public class Location {

    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id; // NanoID 10-char

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "state", nullable = false, length = 50)
    private String state;

    // Constructor without id (id can be generated in service layer)
    public Location(String city, String state) {
        setCity(city);
        setState(state);
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }
}
