
package com.silicon.app.controllers;

import com.silicon.app.models.dto.OrderDTO;
import com.silicon.app.models.entities.Order;
import com.silicon.app.models.requests.OrderCreateRequest;
import com.silicon.app.models.requests.OrderItemRequest;
import com.silicon.app.repositories.UserRepository;
import com.silicon.app.services.LoyaltyService;
import com.silicon.app.services.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для управления заказами пользователя
 * Предоставляет эндпоинты для создания и получения заказов
 */
@RestController
public class OrderController {
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final LoyaltyService loyaltyService;

    public OrderController(UserRepository userRepository, OrderService orderService, LoyaltyService loyaltyService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
        this.loyaltyService = loyaltyService;
    }

    /**
     * Создать новый заказ
     * @param authentication текущий пользователь
     * @param items список позиций в заказе
     * @return созданный заказ
     */
    @PostMapping("/user/order")
    public OrderDTO createOrder(Authentication authentication, @RequestBody List<OrderItemRequest> items) {
        Long userId = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"))
                .getId();
        return OrderDTO.fromEntity(orderService.createOrder(userId, items));
    }

    /**
     * Создать заказ с использованием баллов лояльности
     * @param authentication текущий пользователь
     * @param request request с items и pointsToSpend
     * @return созданный заказ со скидкой
     */
    @PostMapping("/user/order/with-points")
    public OrderDTO createOrderWithPoints(Authentication authentication, @RequestBody OrderCreateRequest request) {
        Long userId = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"))
                .getId();
        Order order = orderService.createOrderWithPointsRedeem(userId, request.items(), request.pointsToSpend());
        return OrderDTO.fromEntity(order);
    }

    /**
     * Получить информацию о лояльности пользователя
     */
    @GetMapping("/user/loyalty/info")
    public LoyaltyService.LoyaltyInfo getLoyaltyInfo(Authentication authentication) {
        Long userId = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"))
                .getId();
        return loyaltyService.getUserLoyaltyInfo(userId);
    }

    /**
     * Получить все заказы пользователя
     * @param userId ID пользователя
     * @return все заказы пользователя
     */
    @GetMapping("/user/orders/{userId}")
    public List<OrderDTO> getUserOrders(@PathVariable Long userId) {
        return orderService.getUserOrders(userId).stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Получить последние заказы пользователя
     * @param userId ID пользователя
     * @param limit максимальное количество заказов (по умолчанию 10)
     * @return последние заказы отсортированные по дате
     */
    @GetMapping("/user/orders/{userId}/recent")
    public List<OrderDTO> getRecentOrders(@PathVariable Long userId,
                                          @RequestParam(defaultValue = "10") int limit) {
        return orderService.getRecentOrders(userId, limit).stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
