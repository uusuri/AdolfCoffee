package com.coffee.app.controllers;

import com.coffee.app.model.Menu;
import com.coffee.app.repositiory.MenuItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coffee")
@CrossOrigin(origins = "*")
public class MenuController {
    private final MenuItemRepository menuItemRepository;

    public MenuController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @PostMapping("/add")
    public void addCoffee(@RequestBody Menu menuItem) {
        menuItemRepository.save(menuItem);
    }

    @GetMapping("/all")
    public List<Menu> getCoffeeList() {
        return menuItemRepository.findAll();
    }
}
