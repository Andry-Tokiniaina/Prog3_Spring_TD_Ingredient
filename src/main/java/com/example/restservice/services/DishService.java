package com.example.restservice.services;

import com.example.restservice.entities.DishIngredient;
import com.example.restservice.entities.Ingredient;
import com.example.restservice.repositories.DishIngredientRepository;
import com.example.restservice.repositories.DishRepository;
import com.example.restservice.entities.Dish;

import java.sql.SQLException;
import java.util.List;

import com.example.restservice.repositories.IngredientRepository;
import org.springframework.stereotype.Service;

@Service
public class DishService {
    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;
    private final DishIngredientRepository dishIngredientRepository;

    public DishService(DishRepository dishRepository, IngredientRepository ingredientRepository,  DishIngredientRepository dishIngredientRepository) {
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
        this.dishIngredientRepository = dishIngredientRepository;
    }

    public List<Dish> getAllDishes() {
        return dishRepository.findDish();
    }

    public Dish getDishById(int id) throws SQLException {
        return dishRepository.findDishById(id);
    }

    public Dish createDish(Dish dish) {
        return dishRepository.saveDish(dish);
    }

    public Dish findDishById(int id) throws SQLException {
        return dishRepository.findDishById(id);
    }

    public List<Dish> findAll() {
        return dishRepository.findDish();
    }

    public List<Dish> findDishWithIngredients() throws SQLException {
        List<Dish> dishes = findAll();

        for (Dish dish : dishes) {
            List<DishIngredient> ingredients = dishIngredientRepository.findIngredientByDishId(dish.getId());
            dish.setDishIngredients(ingredients);
        }

        return dishes;
    }
}
