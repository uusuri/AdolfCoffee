package com.coffee.app.repositiory;

import com.coffee.app.model.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    Coffee findByName(String name);
}
