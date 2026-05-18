package com.silicon.app.repositories;

import com.silicon.app.models.entities.AllergenType;
import com.silicon.app.models.entities.Bakery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с выпечкой
 */
@Repository
public interface BakeryRepository extends JpaRepository<Bakery, Long> {

    /**
     * Получить выпечку без указанных аллергенов
     * @param allergens список аллергенов для исключения
     * @return выпечка которая не содержит ни один из указанных аллергенов
     */
    @Query("SELECT b FROM Bakery b WHERE NOT EXISTS " +
           "(SELECT 1 FROM b.allergens a WHERE a IN :allergens)")
    List<Bakery> findAllWithoutAllergens(List<AllergenType> allergens);
}
