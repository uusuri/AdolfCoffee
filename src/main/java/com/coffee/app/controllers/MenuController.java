package com.coffee.app.controllers;

import com.coffee.app.model.Coffee;
import com.coffee.app.repositiory.CoffeeRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coffee")
@CrossOrigin(origins = "*")
public class MenuController {
    private final CoffeeRepository coffeeRepository;

    public MenuController(CoffeeRepository coffeeRepository) {
        this.coffeeRepository = coffeeRepository;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public void addCoffee(@RequestBody Coffee menuItem) {
        coffeeRepository.save(menuItem);
    }

    @PostMapping("/order")
    @PreAuthorize("hasRole('USER')")
    public void orderCoffee(@RequestParam String coffeeName) {
        Coffee coffee = coffeeRepository.findByName(coffeeName);
        if (coffee != null) {
            coffeeRepository.save(coffee);
        } else {
            throw new RuntimeException("Coffee not found: " + coffeeName);
        }
    }

    @GetMapping("/all")
    public List<Coffee> getCoffeeList() {
        return coffeeRepository.findAll();
    }
}
