package com.example.restservice.entities;

import com.example.restservice.entities.enums.Unit;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import com.example.restservice.entities.enums.CategoryEnum;
import com.example.restservice.entities.enums.MovementTypeEnum;

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

    public StockValue getStockValueAt (Instant t, Unit unit) {
        double total = 0;

        for (StockMovement stockMovement : stockMovementList) {
            if (!stockMovement.getCreationDatetime().isAfter(t)) {
                if(stockMovement.getType() == MovementTypeEnum.IN
                        && stockMovement.getValue().getUnit().equals(unit)) {
                    total += stockMovement.getValue().getQuantity();
                }
                else if (stockMovement.getType() == MovementTypeEnum.OUT
                        && stockMovement.getValue().getUnit().equals(unit)) {
                    total -= stockMovement.getValue().getQuantity();
                }
            }
        }
        return new StockValue(total, unit);
    }
}
