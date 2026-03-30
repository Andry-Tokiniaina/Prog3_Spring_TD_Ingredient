package com.example.restservice.entities;

import com.example.restservice.entities.enums.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DishIngredient {
    private Dish dish;
    private Ingredient ingredient;
    private Double quantity;
    private Unit unit;
}
