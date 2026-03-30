package com.example.restservice.entities;

import com.example.restservice.entities.enums.DishTypeEnum;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Dish {
    private Integer id;
    private Double price;
    private String name;
    private DishTypeEnum dishType;
    private List<DishIngredient> dishIngredients;
}
