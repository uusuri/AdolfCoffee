package com.silicon.app.controllers;

import com.silicon.app.models.dto.TrendingProductDTO;
import com.silicon.app.models.entities.AllergenType;
import com.silicon.app.models.entities.Bakery;
import com.silicon.app.models.entities.Drink;
import com.silicon.app.models.entities.Milk;
import com.silicon.app.models.entities.Syrup;
import com.silicon.app.repositories.BakeryRepository;
import com.silicon.app.repositories.OrderRepository;
import com.silicon.app.services.MenuService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с меню ресторана
 * Предоставляет эндпоинты для получения и фильтрации напитков, выпечки и их
 * ингредиентов
 */
@RestController
@RequestMapping("/menu")
@CrossOrigin(origins = "*")
public class MenuController {
  private final MenuService menuService;
  private final BakeryRepository bakeryRepository;
  private final OrderRepository orderRepository;

  public MenuController(MenuService menuService, BakeryRepository bakeryRepository,
      OrderRepository orderRepository) {
    this.menuService = menuService;
    this.bakeryRepository = bakeryRepository;
    this.orderRepository = orderRepository;
  }

  @GetMapping("/drinks")
  public List<Drink> getDrinkList() {
    return menuService.getAllDrinks();
  }

  @GetMapping("/bakery")
  public List<Bakery> getBakeryList() {
    return menuService.getAllBakery();
  }

  @GetMapping("/syrups")
  public List<Syrup> getSyrups() {
    return menuService.getAllSyrups();
  }

  @GetMapping("/milks")
  public List<Milk> getMilks() {
    return menuService.getAllMilks();
  }

  /**
   * Фильтр выпечки по аллергенам
   *
   * @param allergens список аллергенов для исключения
   * @return выпечка без указанных аллергенов
   */
  @GetMapping("/bakery/filter")
  public List<Bakery> getBakeryWithoutAllergens(@RequestParam(required = false) List<AllergenType> allergens) {
    if (allergens == null || allergens.isEmpty()) {
      return menuService.getAllBakery();
    }
    return bakeryRepository.findAllWithoutAllergens(allergens);
  }

  /**
   * Получить популярные напитки (тренды)
   *
   * @return топ-10 напитков отсортированные по кол-ву покупок
   */
  @GetMapping("/trending")
  public List<TrendingProductDTO> getTrendingProducts() {
    return orderRepository.findMostPopularDrinks(PageRequest.of(0, 10)).stream()
        .map(row -> TrendingProductDTO.fromDrink((Drink) row[0], ((Number) row[1]).longValue()))
        .collect(Collectors.toList());
  }
}
