package com.coffee.app.repositiory;

import com.coffee.app.model.Syrup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyrupRepository extends JpaRepository<Syrup, Long> {
}
