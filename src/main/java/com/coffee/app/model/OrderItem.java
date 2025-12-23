package com.coffee.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Drink drink;

    @ManyToOne
    private Coffee selectedCoffee;

    @ManyToOne
    private Milk selectedMilk;

    @ManyToMany
    private Set<Syrup> selectedSyrups = new HashSet<>();

    private Integer quantity;
    private BigDecimal totalPrice;
}
