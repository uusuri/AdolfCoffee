package com.coffee.app.controllers;

import com.coffee.app.model.MenuModel;
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
    public void addCoffee(@RequestBody MenuModel menuItem) {
        menuItemRepository.save(menuItem);
    }

    @GetMapping("/all")
    public List<MenuModel> getCoffeeList() {
        return menuItemRepository.findAll();
    }
}
