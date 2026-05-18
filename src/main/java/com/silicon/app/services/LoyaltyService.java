package com.silicon.app.services;

import com.silicon.app.models.entities.LoyaltyTier;
import com.silicon.app.models.entities.Order;
import com.silicon.app.models.entities.User;
import com.silicon.app.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Сервис для управления системой лояльности
 * Отвечает за начисление и списание баллов, расчет кэшбэка
 */
@Slf4j
@Service
public class LoyaltyService {
    private final UserRepository userRepository;

    public LoyaltyService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Начислить баллы за покупку
     * Конвертирует 1 рубль в 1 балл * процент кэшбэка
     */
    @Transactional
    public long awardPointsForOrder(Long userId, BigDecimal orderAmount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        long pointsEarned = orderAmount.multiply(user.getCashbackPercentage())
                .setScale(0, java.math.RoundingMode.DOWN).longValue();

        user.addPurchase(orderAmount);
        userRepository.save(user);

        return pointsEarned;
    }

    /**
     * Списать баллы из аккаунта пользователя
     * 1 балл = 1 рубль скидки
     */
    @Transactional
    public BigDecimal redeemPoints(Long userId, long pointsToSpend) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getLoyaltyPoints() < pointsToSpend) {
            throw new IllegalArgumentException(
                    "Insufficient points. Available: " + user.getLoyaltyPoints() +
                    ", Requested: " + pointsToSpend);
        }

        user.spendPoints(pointsToSpend);
        userRepository.save(user);

        // 1 балл = 1 рубль
        return new BigDecimal(pointsToSpend);
    }

    /**
     * Получить информацию о лояльности пользователя
     */
    @Transactional(readOnly = true)
    public LoyaltyInfo getUserLoyaltyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        LoyaltyTier tier = user.getCurrentTier();
        BigDecimal nextTierSpent = tier == LoyaltyTier.PLATINUM ?
                tier.getMinSpent() :
                getLoyaltyTierMinAmount(tier.ordinal() + 1);

        BigDecimal amountToNextTier = nextTierSpent.subtract(user.getTotalSpent());

        // Защита от отрицательного значения если вдруг уже платина
        if (amountToNextTier.compareTo(BigDecimal.ZERO) < 0) {
            amountToNextTier = BigDecimal.ZERO;
        }

        log.info("Loyalty Info - userId: {}, totalSpent: {}, tier: {}, nextTierSpent: {}, amountToNextTier: {}",
                userId, user.getTotalSpent(), tier, nextTierSpent, amountToNextTier);

        return new LoyaltyInfo(
                user.getLoyaltyPoints(),
                user.getTotalSpent(),
                tier,
                user.getCashbackPercentage().multiply(new BigDecimal("100")).intValue(),
                amountToNextTier
        );
    }

    private BigDecimal getLoyaltyTierMinAmount(int tierIndex) {
        LoyaltyTier[] tiers = LoyaltyTier.values();
        return tierIndex < tiers.length ? tiers[tierIndex].getMinSpent() : tiers[tiers.length - 1].getMinSpent();
    }

    /**
     * DTO для информации о лояльности
     */
    public record LoyaltyInfo(
            long loyaltyPoints,
            BigDecimal totalSpent,
            LoyaltyTier currentTier,
            int cashbackPercentage,
            BigDecimal amountToNextTier
    ) {}
}

