package com.coffee.app.repositiory;

import com.coffee.app.model.Drink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface DrinkRepository extends JpaRepository<Drink, Long> {
    Drink findByName(String name);
}
