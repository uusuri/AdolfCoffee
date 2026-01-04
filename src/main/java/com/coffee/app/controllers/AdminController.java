package com.coffee.app.controllers;

import com.coffee.app.model.Coffee;
import com.coffee.app.model.Drink;
import com.coffee.app.model.Milk;
import com.coffee.app.model.Syrup;
import com.coffee.app.repositiory.CoffeeRepository;
import com.coffee.app.repositiory.DrinkRepository;
import com.coffee.app.repositiory.MilkRepository;
import com.coffee.app.repositiory.SyrupRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final DrinkRepository drinkRepository;
    private final CoffeeRepository coffeeRepository;
    private final SyrupRepository syrupRepository;
    private final MilkRepository milkRepository;

    public AdminController(DrinkRepository drinkRepository, CoffeeRepository coffeeRepository,
                           SyrupRepository syrupRepository, MilkRepository milkRepository) {
        this.drinkRepository = drinkRepository;
        this.coffeeRepository = coffeeRepository;
        this.syrupRepository = syrupRepository;
        this.milkRepository = milkRepository;
    }

    @PostMapping("/add/drink")
    @PreAuthorize("hasRole('ADMIN')")
    public void addDrink(@RequestBody Drink drink) {
        if (drink.getDefaultCoffee() != null && drink.getDefaultCoffee().getId() == null) {
            Coffee existingCoffee = coffeeRepository.findByName(drink.getDefaultCoffee().getName());
            if (existingCoffee != null) {
                drink.setDefaultCoffee(existingCoffee);
            }
        }

        if (drink.getDefaultMilk() != null && drink.getDefaultMilk().getId() == null) {
            Milk existingMilk = milkRepository.findByName(drink.getDefaultMilk().getName());
            if (existingMilk != null) {
                drink.setDefaultMilk(existingMilk);
            }
        }
        drinkRepository.save(drink);
    }

    @PostMapping("/add/coffee")
    @PreAuthorize("hasRole('ADMIN')")
    public void addCoffee(@RequestBody Coffee coffee) {
        coffeeRepository.save(coffee);
    }

    @PostMapping("/add/milk")
    @PreAuthorize("hasRole('ADMIN')")
    public void addCoffee(@RequestBody Milk milk) {
        milkRepository.save(milk);
    }

    @PostMapping("/add/syrup")
    @PreAuthorize("hasRole('ADMIN')")
    public void addCoffee(@RequestBody Syrup syrup) {
        syrupRepository.save(syrup);
    }
}
