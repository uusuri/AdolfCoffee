package com.coffee.app.controllers;

import com.coffee.app.model.Drink;
import com.coffee.app.repositiory.DrinkRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coffee")
@CrossOrigin(origins = "*")
public class MenuController {
    private final DrinkRepository drinkRepository;

    public MenuController(DrinkRepository drinkRepository) {
        this.drinkRepository = drinkRepository;
    }

    @PostMapping("/order")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void orderCoffee(@RequestParam String drinkName) {
        Drink drink = drinkRepository.findByName(drinkName);
        if (drink != null) {
            drinkRepository.save(drink);
        } else {
            throw new RuntimeException("Coffee not found: " + drinkName);
        }
    }

    @GetMapping("/all")
    public List<Drink> getDrinkList() {
        return drinkRepository.findAll();
    }
}
