package com.silicon.app.models.dto;

import com.silicon.app.models.entities.Drink;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO для отображения популярных напитков (тренды)
 * Используется в API эндпоинте GET /menu/trending
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrendingProductDTO {
    private Long id;
    private String name;
    private String image;
    private BigDecimal basePrice;
    private String drinkBaseName;
    private Long purchaseCount;
    private String trend;

    /**
     * Конвертирует Drink в TrendingProductDTO
     * @param drink - напиток из базы данных
     * @param purchaseCount - количество покупок напитка
     * @return TrendingProductDTO готовый для отправки в API
     */
    public static TrendingProductDTO fromDrink(Drink drink, Long purchaseCount) {
        TrendingProductDTO dto = new TrendingProductDTO();
        dto.setId(drink.getId());
        dto.setName(drink.getName());
        dto.setImage(drink.getImage());
        dto.setBasePrice(drink.getBasePrice());
        dto.setDrinkBaseName(drink.getDrinkBase() != null ? drink.getDrinkBase().getName() : "N/A");
        dto.setPurchaseCount(purchaseCount);
        dto.setTrend("🔥 Популярно");
        return dto;
    }
}
