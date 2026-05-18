package com.silicon.app.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "bakery")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Bakery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private BigDecimal price;

    private String description;

    @ElementCollection(targetClass = AllergenType.class)
    @CollectionTable(
            name = "bakery_allergens",
            joinColumns = @JoinColumn(name = "bakery_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "allergen")
    private List<AllergenType> allergens;
}
