package com.silicon.app.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "drinks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Drink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String image;
    private BigDecimal basePrice;

    @ManyToOne
    private DrinkBase drinkBase;

    @ManyToOne
    private Milk milk;

    @ManyToMany
    @JoinTable(
            name = "drink_default_syrups",
            joinColumns = @JoinColumn(name = "drink_id"),
            inverseJoinColumns = @JoinColumn(name = "syrup_id")
    )
    private List<Syrup> defaultSyrups = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "drink_additional_syrups",
            joinColumns = @JoinColumn(name = "drink_id"),
            inverseJoinColumns = @JoinColumn(name = "syrup_id")
    )
    private List<Syrup> additionalSyrups = new ArrayList<>();
}
