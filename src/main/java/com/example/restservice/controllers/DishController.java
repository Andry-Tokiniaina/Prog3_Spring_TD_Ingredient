package com.example.restservice.controllers;

import com.example.restservice.entities.Dish;
import com.example.restservice.entities.Ingredient;
import com.example.restservice.services.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController("/dishes")
public class DishController {
    DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public List<Dish> getDishes() throws SQLException {
        return dishService.findDishWithIngredients();
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(
            @PathVariable Integer id,
            @RequestBody(required = false) List<Ingredient> ingredients
    ) {

        if (ingredients == null) {
            return ResponseEntity.badRequest()
                    .body("Request body is required.");
        }

        try {
            Dish dish = dishService.findDishById(id);

            if (dish == null) {
                return ResponseEntity.status(404)
                        .body("Dish.id=" + id + " is not found");
            }

            return ResponseEntity.ok("Ingredients updated");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
