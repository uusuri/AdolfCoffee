package com.silicon.app.models.dto;

import com.silicon.app.models.entities.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO для передачи информации о заказе в API
 * Содержит полную информацию о заказе и его позициях
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private Long userId;
    private List<OrderItemDTO> orderItems;
    private String status;
    private LocalDateTime date;
    private BigDecimal totalAmount;
    private Long pointsEarned;
    private Long pointsSpent;
    private BigDecimal discountFromPoints;

    /**
     * Конвертирует Order entity в OrderDTO
     * @param order заказ из базы данных
     * @return DTO готовый для отправки в API
     */
    public static OrderDTO fromEntity(Order order) {
        List<OrderItemDTO> items = order.getOrderItems() != null
                ? order.getOrderItems().stream()
                .map(OrderItemDTO::fromEntity)
                .collect(Collectors.toList())
                : List.of();

        return new OrderDTO(
                order.getId(),
                order.getUserId(),
                items,
                order.getStatus(),
                order.getDate(),
                order.getTotalAmount(),
                order.getPointsEarned(),
                order.getPointsSpent(),
                order.getDiscountFromPoints()
        );
    }
}
