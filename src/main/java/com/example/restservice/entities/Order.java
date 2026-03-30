package com.example.restservice.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Order {
    private Integer id;
    private String reference;
    private Instant creationDatetime;
    private List<DishOrder> dishOrderList;
}
