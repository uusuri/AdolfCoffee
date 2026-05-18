package com.silicon.app.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Long loyaltyPoints = 0L;

    @Column(nullable = false)
    private BigDecimal totalSpent = BigDecimal.ZERO;

    /**
     * Получить текущий уровень лояльности на основе потраченных денег
     */
    public LoyaltyTier getCurrentTier() {
        return LoyaltyTier.fromTotalSpent(totalSpent);
    }

    /**
     * Получить процент кэшбэка в зависимости от уровня
     */
    public BigDecimal getCashbackPercentage() {
        return getCurrentTier().getCashbackRate();
    }

    /**
     * Добавить баллы и обновить totalSpent
     */
    public void addPurchase(BigDecimal amount) {
        this.totalSpent = this.totalSpent.add(amount);
        long pointsToAdd = amount.multiply(getCashbackPercentage()).longValue();
        this.loyaltyPoints = this.loyaltyPoints + pointsToAdd;
    }

    /**
     * Списать баллы (проверка делается в сервисе)
     */
    public void spendPoints(long points) {
        this.loyaltyPoints = this.loyaltyPoints - points;
    }
}
