package com.coffee.app.repositiory;

import com.coffee.app.model.Milk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MilkRepository extends JpaRepository<Milk, Long> {
    Milk findByName(String name);
}
