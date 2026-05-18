package com.silicon.app.services;

import com.silicon.app.models.entities.Bakery;
import com.silicon.app.models.entities.Drink;
import com.silicon.app.models.entities.Milk;
import com.silicon.app.models.entities.Order;
import com.silicon.app.models.entities.OrderItem;
import com.silicon.app.models.entities.ProductType;
import com.silicon.app.models.entities.Syrup;
import com.silicon.app.models.entities.User;
import com.silicon.app.models.requests.OrderItemRequest;
import com.silicon.app.repositories.BakeryRepository;
import com.silicon.app.repositories.DrinkRepository;
import com.silicon.app.repositories.MilkRepository;
import com.silicon.app.repositories.OrderRepository;
import com.silicon.app.repositories.SyrupRepository;
import com.silicon.app.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для работы с заказами
 * Управляет созданием, получением и расчетом стоимости заказов
 */
@Service
public class OrderService {
    private final DrinkRepository drinkRepository;
    private final BakeryRepository bakeryRepository;
    private final OrderRepository orderRepository;
    private final MilkRepository milkRepository;
    private final SyrupRepository syrupRepository;
    private final UserRepository userRepository;
    private final LoyaltyService loyaltyService;

    public OrderService(DrinkRepository drinkRepository, BakeryRepository bakeryRepository,
                        OrderRepository orderRepository, MilkRepository milkRepository, SyrupRepository syrupRepository,
                        UserRepository userRepository, LoyaltyService loyaltyService) {
        this.drinkRepository = drinkRepository;
        this.bakeryRepository = bakeryRepository;
        this.orderRepository = orderRepository;
        this.milkRepository = milkRepository;
        this.syrupRepository = syrupRepository;
        this.userRepository = userRepository;
        this.loyaltyService = loyaltyService;
    }

    /**
     * Расчет дополнительной цены за сироп (если он не входит в базовый набор)
     */
    private BigDecimal calculateSyrupPrice(List<Long> syrupIds, Set<Long> defaultIds) {
        if (syrupIds == null || syrupIds.isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<Syrup> syrups = syrupRepository.findAllById(syrupIds);
        return syrups.stream()
                .filter(syrup -> !defaultIds.contains(syrup.getId()))
                .map(Syrup::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Расчет цены напитка с учетом молока и сиропов
     */
    private BigDecimal calculateDrinkPrice(Drink drink, OrderItemRequest item) {
        BigDecimal price = drink.getBasePrice();

        // Добавляем цену молока, если оно отличается от дефолтного
        if (item.getMilkId() != null && drink.getMilk() != null
                && !Objects.equals(drink.getMilk().getId(), item.getMilkId())) {
            Milk milk = milkRepository.findById(item.getMilkId())
                    .orElseThrow(() -> new EntityNotFoundException("Milk not found"));
            price = price.add(milk.getPrice());
        }

        Set<Long> defaultIds = drink.getDefaultSyrups()
                .stream()
                .map(Syrup::getId)
                .collect(Collectors.toSet());

        price = price.add(calculateSyrupPrice(item.getSyrupIds(), defaultIds));

        return price.multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    /**
     * Расчет общей стоимости заказа
     */
    private BigDecimal calculateTotalPrice(List<OrderItemRequest> orderItems) {
        return orderItems.stream()
                .map(item -> {
                    if (item.getProductType() == ProductType.DRINK) {
                        Drink drink = drinkRepository.findById(item.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Drink not found"));
                        return calculateDrinkPrice(drink, item);
                    }
                    if (item.getProductType() == ProductType.BAKERY) {
                        Bakery bakery = bakeryRepository.findById(item.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Bakery not found"));
                        return bakery.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Создание позиции заказа для напитка
     */
    private OrderItem createDrinkOrderItem(OrderItemRequest item) {
        Drink drink = drinkRepository.findById(item.getId())
                .orElseThrow(() -> new EntityNotFoundException("Drink not found"));

        OrderItem orderItem = new OrderItem();
        orderItem.setDrink(drink);
        orderItem.setQuantity(item.getQuantity());
        orderItem.setTotalPrice(calculateDrinkPrice(drink, item));

        return orderItem;
    }

    /**
     * Создание позиции заказа для выпечки
     */
    private OrderItem createBakeryOrderItem(OrderItemRequest item) {
        Bakery bakery = bakeryRepository.findById(item.getId())
                .orElseThrow(() -> new EntityNotFoundException("Bakery not found"));

        OrderItem orderItem = new OrderItem();
        orderItem.setBakery(bakery);
        orderItem.setQuantity(item.getQuantity());
        orderItem.setTotalPrice(bakery.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

        return orderItem;
    }

    /**
     * Создать новый заказ
     */
    @Transactional
    public Order createOrder(Long userId, List<OrderItemRequest> orderItems) {
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("PENDING");
        order.setDate(LocalDateTime.now());
        order.setTotalAmount(calculateTotalPrice(orderItems));

        orderItems.forEach(item -> {
            OrderItem orderItem = item.getProductType() == ProductType.DRINK
                    ? createDrinkOrderItem(item)
                    : createBakeryOrderItem(item);
            order.add(orderItem);
        });

        // Начислить баллы за заказ
        long pointsEarned = loyaltyService.awardPointsForOrder(userId, order.getTotalAmount());
        order.setPointsEarned(pointsEarned);

        return orderRepository.save(order);
    }

    /**
     * Создать заказ с возможностью списания баллов
     * Если pointsToSpend > 0, скидка будет применена и баллы списаны
     */
    @Transactional
    public Order createOrderWithPointsRedeem(Long userId, List<OrderItemRequest> orderItems, long pointsToSpend) {
        BigDecimal orderAmount = calculateTotalPrice(orderItems);
        BigDecimal discount = BigDecimal.ZERO;

        // Если пользователь хочет потратить баллы
        if (pointsToSpend > 0) {
            discount = loyaltyService.redeemPoints(userId, pointsToSpend);
            orderAmount = orderAmount.subtract(discount);

            // Защита от отрицательной суммы
            if (orderAmount.compareTo(BigDecimal.ZERO) < 0) {
                orderAmount = BigDecimal.ZERO;
            }
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("PENDING");
        order.setDate(LocalDateTime.now());
        order.setTotalAmount(orderAmount);
        order.setPointsSpent(pointsToSpend);
        order.setDiscountFromPoints(discount);

        orderItems.forEach(item -> {
            OrderItem orderItem = item.getProductType() == ProductType.DRINK
                    ? createDrinkOrderItem(item)
                    : createBakeryOrderItem(item);
            order.add(orderItem);
        });

        // Начислить баллы за оставшуюся сумму (после скидки)
        long pointsEarned = loyaltyService.awardPointsForOrder(userId, orderAmount);
        order.setPointsEarned(pointsEarned);

        return orderRepository.save(order);
    }

    /**
     * Получить все заказы пользователя
     */
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    /**
     * Получить последние заказы пользователя
     */
    @Transactional(readOnly = true)
    public List<Order> getRecentOrders(Long userId, int limit) {
        return orderRepository.findByUserIdOrderByDateDesc(userId, PageRequest.of(0, limit));
    }
}
