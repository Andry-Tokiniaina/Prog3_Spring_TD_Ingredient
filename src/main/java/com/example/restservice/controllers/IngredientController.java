package com.example.restservice.controllers;

import com.example.restservice.entities.Ingredient;
import com.example.restservice.entities.StockValue;
import com.example.restservice.entities.enums.Unit;
import com.example.restservice.repositories.IngredientRepository;
import com.example.restservice.services.IngredientService;
import com.example.restservice.validator.ParamValidator;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private ParamValidator paramValidator;
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients(){
        List<Ingredient> ingredients = ingredientService.findIngredients();
        return ResponseEntity.ok().body(ingredients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredientById(@PathVariable Integer id) {
        Ingredient ingredient = ingredientService.findIngredientById(id);

        if (ingredient == null) {
            return ResponseEntity.status(404)
                    .body("Ingredient.id=" + id + " is not found");
        }

        return ResponseEntity.ok(ingredient);
    }

    @GetMapping("/ingredients/{id}/stock")
    public ResponseEntity<?> getIngredientStock(
            @PathVariable int id,
            @RequestParam(required = false) String at,
            @RequestParam(required = false) String unit){
        StockValue stockValue;
        try {
            paramValidator.paramValidator(at, unit);
            Instant time = Instant.parse(at);
            Unit unitType = Unit.valueOf(unit);

            stockValue = ingredientService.getStockValueAt(id, time, unitType);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(stockValue);
        }
        catch (BadRequestException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "text/plain")
                    .body(e.getMessage());
        }
    }
}