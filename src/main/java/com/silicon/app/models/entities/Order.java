package com.silicon.app.models.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<OrderItem> orderItems = new ArrayList<>();

    private String status;
    private LocalDateTime date;
    private BigDecimal totalAmount;
    
    // Поля для системы лояльности
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long pointsEarned = 0L;
    
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long pointsSpent = 0L;
    
    @Column(nullable = false, columnDefinition = "NUMERIC(19,2) DEFAULT 0")
    private BigDecimal discountFromPoints = BigDecimal.ZERO;

    public void add(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
}
