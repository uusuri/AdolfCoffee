package com.coffee.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;

@SpringBootApplication
public class AdolfCoffeeApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdolfCoffeeApplication.class, args);
        
    }
}
