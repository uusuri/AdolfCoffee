package com.silicon.app.models.requests;

import java.util.List;

/**
 * Request для создания заказа с поддержкой списания баллов
 */
public record OrderCreateRequest(
    List<OrderItemRequest> items,
    long pointsToSpend // сколько баллов потратить (опционально, по умолчанию 0)
) {
}
