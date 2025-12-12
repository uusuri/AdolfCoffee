package com.coffee.app.repositiory;

import com.coffee.app.model.Menu;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface MenuItemRepository extends JpaRepository<Menu, Long> {

}
