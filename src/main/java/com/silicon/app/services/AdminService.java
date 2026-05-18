package com.silicon.app.services;

import com.silicon.app.models.entities.*;
import com.silicon.app.models.requests.DrinkCreateRequest;
import com.silicon.app.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final DrinkRepository drinkRepository;
    private final BakeryRepository bakeryRepository;
    private final DrinkBaseRepository drinkBaseRepository;
    private final MilkRepository milkRepository;
    private final SyrupRepository syrupRepository;

    public List<Drink> getAllDrinks() {
        return drinkRepository.findAll();
    }

    public List<Bakery> getAllBakery() {
        return bakeryRepository.findAll();
    }

    public List<DrinkBase> getAllCoffee() {
        return drinkBaseRepository.findAll();
    }

    public List<Milk> getAllMilks() {
        return milkRepository.findAll();
    }

    public List<Syrup> getAllSyrups() {
        return syrupRepository.findAll();
    }

    @Transactional
    public Drink createDrink(DrinkCreateRequest request) {
        validateNameAndPrice(request.getName(), request.getBasePrice());

        Drink drink = new Drink();
        parseDrinkFromDrinkCreateRequest(request, drink);

        if (request.getDefaultSyrupIds() != null && !request.getDefaultSyrupIds().isEmpty()) {
            List<Syrup> syrups = syrupRepository.findAllById(request.getDefaultSyrupIds());
            if (syrups.size() != request.getDefaultSyrupIds().size()) {
                throw new EntityNotFoundException("One or more syrups not found");
            }
            drink.getDefaultSyrups().addAll(syrups);
        }
        Drink saved = drinkRepository.save(drink);
        return drinkRepository.findById(saved.getId())
                .orElseThrow(() -> new EntityNotFoundException("Drink not found after save"));
    }

    private void parseDrinkFromDrinkCreateRequest(DrinkCreateRequest request, Drink drink) {
        drink.setName(request.getName());
        drink.setImage(request.getImage());
        drink.setBasePrice(request.getBasePrice());

        log.info("Parsing drink request: drinkBaseId={}, milkId={}", request.getDrinkBaseId(), request.getMilkId());

        if (request.getDrinkBaseId() != null) {
            drink.setDrinkBase(resolveDrinkBase(request.getDrinkBaseId()));
        } else {
            drink.setDrinkBase(getDefaultDrinkBase());
        }

        if (request.getMilkId() != null) {
            Milk milk = resolveMilkId(request.getMilkId());
            log.info("Setting milk: id={}, name={}", milk.getId(), milk.getName());
            drink.setMilk(milk);
        } else {
            log.info("milkId is null — setting milk to null");
            drink.setMilk(null);
        }
    }

    private DrinkBase resolveDrinkBase(Long coffeeId) {
        return drinkBaseRepository.findById(coffeeId)
                .orElseThrow(() -> new EntityNotFoundException("Coffee not found"));
    }

    private DrinkBase getDefaultDrinkBase() {
        return drinkBaseRepository.findByIsDefaultTrue()
                .orElseThrow(() -> new IllegalStateException("Default coffee not configured. Please set one coffee as default in database."));
    }

    private Milk resolveMilkId(Long milkId) {
        return milkRepository.findById(milkId)
                .orElseThrow(() -> new EntityNotFoundException("Milk not found"));
    }

    public void validateNameAndPrice(String name, BigDecimal price) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Bakery name is required");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be a positive value");
        }
    }

    @Transactional
    public Bakery createBakery(Bakery bakery) {
        validateNameAndPrice(bakery.getName(), bakery.getPrice());
        return bakeryRepository.save(bakery);
    }

    @Transactional
    public DrinkBase createDrinkBase(DrinkBase drinkBase) {
        validateNameAndPrice(drinkBase.getName(), drinkBase.getPrice());
        return drinkBaseRepository.save(drinkBase);
    }

    @Transactional
    public Milk createMilk(Milk milk) {
        validateNameAndPrice(milk.getName(), milk.getPrice());
        return milkRepository.save(milk);
    }

    @Transactional
    public Syrup createSyrup(Syrup syrup) {
        validateNameAndPrice(syrup.getName(), syrup.getPrice());
        return syrupRepository.save(syrup);
    }

    @Transactional
    public Drink updateDrink(Long id, DrinkCreateRequest request) {
        Drink existing = drinkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Drink not found: " + id));
        validateNameAndPrice(request.getName(), request.getBasePrice());

        parseDrinkFromDrinkCreateRequest(request, existing);

        if (request.getDefaultSyrupIds() != null && !request.getDefaultSyrupIds().isEmpty()) {
            List<Syrup> syrups = syrupRepository.findAllById(request.getDefaultSyrupIds());
            if (syrups.size() != request.getDefaultSyrupIds().size()) {
                throw new EntityNotFoundException("One or more syrups not found");
            }
            existing.getDefaultSyrups().clear();
            existing.getDefaultSyrups().addAll(syrups);
        } else {
            existing.getDefaultSyrups().clear();
        }

        drinkRepository.save(existing);
        return drinkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Drink not found after update"));
    }

    @Transactional
    public Bakery updateBakery(Long id, Bakery bakery) {
        Bakery existing = bakeryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bakery not found: " + id));
        validateNameAndPrice(bakery.getName(), bakery.getPrice());

        existing.setName(bakery.getName());
        existing.setPrice(bakery.getPrice());
        existing.setImage(bakery.getImage());
        existing.setDescription(bakery.getDescription());

        // Обновляем аллергены
        if (bakery.getAllergens() != null) {
            existing.setAllergens(bakery.getAllergens());
            log.info("Updated allergens for bakery {}: {}", id, bakery.getAllergens());
        } else {
            existing.setAllergens(null);
        }

        return bakeryRepository.save(existing);
    }

    @Transactional
    public DrinkBase updateDrinkBase(Long id, DrinkBase drinkBase) {
        DrinkBase existing = drinkBaseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coffee not found: " + id));
        validateNameAndPrice(drinkBase.getName(), drinkBase.getPrice());

        if (Boolean.TRUE.equals(drinkBase.getIsDefault())) {
            drinkBaseRepository.findAll().forEach(db -> {
                if (!db.getId().equals(id)) {
                    db.setIsDefault(false);
                }
            });
        }

        existing.setName(drinkBase.getName());
        existing.setPrice(drinkBase.getPrice());
        existing.setDescription(drinkBase.getDescription());
        existing.setIsDefault(drinkBase.getIsDefault());

        return drinkBaseRepository.save(existing);
    }

    @Transactional
    public Milk updateMilk(Long id, Milk milk) {
        Milk existing = milkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Milk not found: " + id));
        validateNameAndPrice(milk.getName(), milk.getPrice());

        existing.setName(milk.getName());
        existing.setPrice(milk.getPrice());
        existing.setDescription(milk.getDescription());

        return milkRepository.save(existing);
    }

    @Transactional
    public Syrup updateSyrup(Long id, Syrup syrup) {
        Syrup existing = syrupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Syrup not found: " + id));
        validateNameAndPrice(syrup.getName(), syrup.getPrice());

        existing.setName(syrup.getName());
        existing.setPrice(syrup.getPrice());
        existing.setDescription(syrup.getDescription());

        return syrupRepository.save(existing);
    }

    @Transactional
    public void deleteDrink(Long id) {
        if (!drinkRepository.existsById(id)) {
            throw new EntityNotFoundException("Drink not found: " + id);
        }
        drinkRepository.deleteById(id);
    }

    @Transactional
    public void deleteBakery(Long id) {
        if (!bakeryRepository.existsById(id)) {
            throw new EntityNotFoundException("Bakery not found: " + id);
        }
        bakeryRepository.deleteById(id);
    }

    @Transactional
    public void deleteDrinkBase(Long id) {
        if (!drinkBaseRepository.existsById(id)) {
            throw new EntityNotFoundException("Coffee not found: " + id);
        }
        drinkBaseRepository.deleteById(id);
    }

    @Transactional
    public void deleteMilk(Long id) {
        if (!milkRepository.existsById(id)) {
            throw new EntityNotFoundException("Milk not found: " + id);
        }
        milkRepository.deleteById(id);
    }

    @Transactional
    public void deleteSyrup(Long id) {
        Syrup syrup = syrupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Syrup not found"));

        List<Drink> drinksWithSyrup = drinkRepository.findAll();
        for (Drink drink : drinksWithSyrup) {
            drink.getDefaultSyrups().remove(syrup);
        }
        drinkRepository.saveAll(drinksWithSyrup);

        syrupRepository.delete(syrup);
    }

}
