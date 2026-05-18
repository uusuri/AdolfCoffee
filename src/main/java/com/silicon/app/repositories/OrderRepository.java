package com.silicon.app.repositories;

import com.silicon.app.models.entities.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с заказами
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Получить все заказы пользователя
     * @param id ID пользователя
     * @return список заказов пользователя
     */
    List<Order> findAllByUserId(Long id);

    /**
     * Получить последние заказы пользователя (отсортированные по дате)
     * @param userId ID пользователя
     * @param pageable параметры пагинации (limit)
     * @return список последних заказов
     */
    List<Order> findByUserIdOrderByDateDesc(Long userId, Pageable pageable);

    /**
     * Получить самые популярные напитки по кол-ву покупок
     * @param pageable параметры пагинации (limit)
     * @return массив [Drink, purchaseCount] отсортированный по убыванию количества покупок
     */
    @Query("SELECT oi.drink, COUNT(oi) as purchaseCount " +
           "FROM OrderItem oi " +
           "WHERE oi.drink IS NOT NULL " +
           "GROUP BY oi.drink " +
           "ORDER BY COUNT(oi) DESC")
    List<Object[]> findMostPopularDrinks(Pageable pageable);
}
