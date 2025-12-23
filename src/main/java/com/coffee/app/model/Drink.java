package com.coffee.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "drinks")
@Getter
@Setter
public class Drink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double basePrice;

    @ManyToOne
    private Coffee defaultCoffee;

    @ManyToOne
    private Milk defaultMilk;

    @ManyToMany
    private Set<Syrup> defaultSyrups = new HashSet<>();
}
