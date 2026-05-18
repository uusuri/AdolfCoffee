package com.silicon.app.models.dto;

import com.silicon.app.models.entities.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO для передачи информации о позиции в заказе
 * Содержит информацию о напитке или выпечке и её количество
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long drinkId;
    private String drinkName;
    private Long bakeryId;
    private String bakeryName;
    private Integer quantity;
    private BigDecimal totalPrice;

    /**
     * Конвертирует OrderItem entity в OrderItemDTO
     * @param orderItem позиция заказа из базы данных
     * @return DTO готовый для отправки в API
     */
    public static OrderItemDTO fromEntity(OrderItem orderItem) {
        return new OrderItemDTO(
                orderItem.getId(),
                orderItem.getDrink() != null ? orderItem.getDrink().getId() : null,
                orderItem.getDrink() != null ? orderItem.getDrink().getName() : null,
                orderItem.getBakery() != null ? orderItem.getBakery().getId() : null,
                orderItem.getBakery() != null ? orderItem.getBakery().getName() : null,
                orderItem.getQuantity(),
                orderItem.getTotalPrice()
        );
    }
}
