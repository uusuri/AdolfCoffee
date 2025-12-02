package com.coffee.app.repositiory;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CoffeeRepository {
    private final ArrayList<String> coffeeList = new ArrayList<>();

    public void addCoffee(String coffee) {
        this.coffeeList.add(coffee);
    }

    public ArrayList<String> getAllCoffee() {
        return new ArrayList<>(coffeeList); // возвращаем копию
    }
}
