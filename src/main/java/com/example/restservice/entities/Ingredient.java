package com.example.restservice.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.util.List;
import com.example.restservice.entities.enums.CategoryEnum;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;
    private List<StockMovement> stockMovementList;
}
