package com.example.restservice.controllers;

import com.example.restservice.entities.Dish;
import com.example.restservice.exception.NotFoundException;
import com.example.restservice.services.DishService;
import com.example.restservice.validator.RequestBodyValidator;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController("/dishes")
public class DishController {
    DishService dishService;
    RequestBodyValidator requestBodyValidator;

    public DishController(DishService dishService, RequestBodyValidator requestBodyValidator) {
        this.dishService = dishService;
        this.requestBodyValidator = requestBodyValidator;
    }

    @GetMapping
    public List<Dish> getDishes() throws SQLException {
        return dishService.findDishWithIngredients();
    }

    @PutMapping("/dishes/{id}/ingredients")
    public ResponseEntity<?> updateIngredients(
            @PathVariable Integer id,
            @RequestBody List<Integer> ingId) {
        try {
            requestBodyValidator.requestBodyValidator(ingId);
            Dish dish = dishService.updateIngredient(id, ingId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(dish);
        }
        catch (BadRequestException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "text/plain")
                    .body(e.getMessage());
        }
        catch (NotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .header("Content-Type", "text/plain")
                    .body(e.getMessage());
        }
        catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "text/plain")
                    .body(e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
