package com.coffee.app.controllers;

import com.coffee.app.model.Coffee;
import com.coffee.app.repositiory.CoffeeRepository;
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
    public void addCoffee(@RequestBody Coffee menuItem) {
        coffeeRepository.save(menuItem);
    }

    @GetMapping("/all")
    public List<Coffee> getCoffeeList() {
        return coffeeRepository.findAll();
    }
}
