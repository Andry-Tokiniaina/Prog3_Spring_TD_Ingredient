package com.example.restservice.entities;

import com.example.restservice.entities.enums.MovementTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {
    private Integer id;
    private MovementTypeEnum type;
    private Instant creationDatetime;
    private StockValue value;
}
