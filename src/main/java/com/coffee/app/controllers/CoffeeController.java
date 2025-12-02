package com.coffee.app.controllers;

import com.coffee.app.repositiory.CoffeeRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/coffee")
public class CoffeeController {
    private final CoffeeRepository coffeeRepository;

    public CoffeeController(CoffeeRepository coffeeRepository) {
        this.coffeeRepository = coffeeRepository;
    }

    @PostMapping("/add")
    public void addCoffee(@RequestParam String coffeeName) {
        coffeeRepository.addCoffee(coffeeName);
    }

    @GetMapping("/all")
    public ArrayList<String> getCoffeeList() {
        return coffeeRepository.getAllCoffee();
    }
}
