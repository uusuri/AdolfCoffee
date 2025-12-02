package com.coffee.app.config;

import com.coffee.app.repositiory.CoffeeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    CoffeeRepository getCoffeeRepository() {
        return new CoffeeRepository();
    }
}
