package com.coffee.app.repositiory;

import com.coffee.app.model.Milk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MilkRepository extends JpaRepository<Milk, Long> {
}
