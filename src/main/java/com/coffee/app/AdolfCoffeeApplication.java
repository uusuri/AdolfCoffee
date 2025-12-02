package com.coffee.app;

import com.coffee.app.repositiory.CoffeeRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class AdolfCoffeeApplication {
    public static void main(String[] args) {
        CoffeeRepository repository;
        SpringApplication.run(AdolfCoffeeApplication.class, args);
    }
}