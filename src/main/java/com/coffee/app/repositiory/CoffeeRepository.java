package com.coffee.app.repositiory;

import com.coffee.app.model.Coffee;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    Coffee findByName(String name);
}
