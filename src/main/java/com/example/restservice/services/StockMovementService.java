package com.example.restservice.services;

import com.example.restservice.entities.Ingredient;
import com.example.restservice.entities.StockMovement;
import com.example.restservice.repositories.IngredientRepository;
import com.example.restservice.repositories.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class StockMovementService {

    private final IngredientRepository ingredientRepository;
    private final StockMovementRepository stockMovementRepository;

    public StockMovementService(IngredientRepository ingredientRepository,
                                StockMovementRepository stockMovementRepository) {
        this.ingredientRepository = ingredientRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<StockMovement> getStockMovements(Integer ingredientId, Instant from, Instant to) {
        Ingredient ingredient = ingredientRepository.findIngredientById(ingredientId);
        if (ingredient == null) {
            return null;
        }

        return stockMovementRepository.findByIngredientAndDateRange(ingredientId, from, to);
    }

    public List<StockMovement> createStockMovements(
            Integer ingredientId, List<StockMovement> movementsToCreate
    ) {
        Ingredient ingredient = ingredientRepository.findIngredientById(ingredientId);
        if (ingredient == null) {
            return null;
        }

        return stockMovementRepository.saveStockMovements(ingredientId, movementsToCreate);
    }
}

