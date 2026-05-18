package com.silicon.app.models.entities;

import java.math.BigDecimal;

/**
 * Enum для определения уровней лояльности
 * Каждый уровень имеет минимальную сумму покупок и процент кэшбэка
 */
public enum LoyaltyTier {
  BRONZE("Bronze", new BigDecimal("0"), new BigDecimal("0.02")),
  SILVER("Silver", new BigDecimal("3000"), new BigDecimal("0.03")),
  GOLD("Gold", new BigDecimal("10000"), new BigDecimal("0.04")),
  PLATINUM("Platinum", new BigDecimal("20000"), new BigDecimal("0.05"));

  private final String name;
  private final BigDecimal minSpent;
  private final BigDecimal cashbackRate;

  LoyaltyTier(String name, BigDecimal minSpent, BigDecimal cashbackRate) {
    this.name = name;
    this.minSpent = minSpent;
    this.cashbackRate = cashbackRate;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getMinSpent() {
    return minSpent;
  }

  public BigDecimal getCashbackRate() {
    return cashbackRate;
  }

  /**
   * Получить уровень лояльности по сумме потраченных денег
   */
  public static LoyaltyTier fromTotalSpent(BigDecimal totalSpent) {
    if (totalSpent.compareTo(PLATINUM.minSpent) >= 0)
      return PLATINUM;
    if (totalSpent.compareTo(GOLD.minSpent) >= 0)
      return GOLD;
    if (totalSpent.compareTo(SILVER.minSpent) >= 0)
      return SILVER;
    return BRONZE;
  }
}
