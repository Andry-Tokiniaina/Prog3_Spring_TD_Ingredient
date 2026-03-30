package com.example.restservice.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DishOrder {
    private Integer id;
    private Dish dish;
    private Integer quantity;
}
