package com.example.restservice.services;

import com.example.restservice.entities.Ingredient;
import com.example.restservice.entities.StockValue;
import com.example.restservice.entities.enums.Unit;
import com.example.restservice.repositories.IngredientRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class IngredientService {
    IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> findIngredients() {
        return this.ingredientRepository.findIngredients();
    }
    public Ingredient findIngredientById(int id) {
        return this.ingredientRepository.findIngredientById(id);
    }
    public StockValue getStockValueAt (int id, Instant at, Unit unit){
        Ingredient ingredient = findIngredientById(id);

        return ingredient.getStockValueAt(at, unit);
    }
}
