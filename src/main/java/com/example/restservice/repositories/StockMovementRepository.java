package com.example.restservice.repositories;

import com.example.restservice.DataSource;
import com.example.restservice.entities.StockMovement;
import com.example.restservice.entities.StockValue;
import com.example.restservice.entities.enums.MovementTypeEnum;
import com.example.restservice.entities.enums.Unit;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StockMovementRepository {
    List<StockMovement> findStockMovementsByIngredientId(Integer id) throws SQLException {
        DataSource dataSource = new DataSource();
        Connection connection = dataSource.getConnection();
        List<StockMovement> stockMovementList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            select id, quantity, unit, type, creation_datetime
                            from stock_movement
                            where stock_movement.id_ingredient = ?;
                            """);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                StockMovement stockMovement = new StockMovement();
                stockMovement.setId(resultSet.getInt("id"));
                stockMovement.setType(MovementTypeEnum.valueOf(resultSet.getString("type")));
                stockMovement.setCreationDatetime(resultSet.getTimestamp("creation_datetime").toInstant());

                StockValue stockValue = new StockValue();
                stockValue.setQuantity(resultSet.getDouble("quantity"));
                stockValue.setUnit(Unit.valueOf(resultSet.getString("unit")));
                stockMovement.setValue(stockValue);

                stockMovementList.add(stockMovement);
            }
            dataSource.closeConnection(connection);
            return stockMovementList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<StockMovement> findByIngredientAndDateRange(
            Integer ingredientId,
            Instant from,
            Instant to
    ) {
        List<StockMovement> movements = new ArrayList<>();
        DataSource ds = new DataSource();

        try (Connection conn = ds.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("""
            SELECT id, movement_type, creation_datetime, quantity, unit
            FROM stock_movement
            WHERE ingredient_id = ?
            AND creation_datetime BETWEEN ? AND ?
            ORDER BY creation_datetime
        """);

            ps.setInt(1, ingredientId);
            ps.setTimestamp(2, Timestamp.from(from));
            ps.setTimestamp(3, Timestamp.from(to));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                StockMovement sm = new StockMovement();

                sm.setId(rs.getInt("id"));
                sm.setType(MovementTypeEnum.valueOf(rs.getString("movement_type")));
                sm.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());

                StockValue value = new StockValue();
                value.setQuantity(rs.getDouble("quantity"));
                value.setUnit(Unit.valueOf(rs.getString("unit")));

                sm.setValue(value);

                movements.add(sm);
            }

            return movements;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<StockMovement> saveStockMovements(Integer ingredientId, List<StockMovement> movementsToCreate) {
        List<StockMovement> createdMovements = new ArrayList<>();
        DataSource ds = new DataSource();

        String insertSql = """
        INSERT INTO stock_movement (ingredient_id, quantity, unit, movement_type, creation_datetime)
        VALUES (?, ?, ?::unit, ?::movement_type, ?)
        RETURNING id, creation_datetime
    """;

        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (StockMovement sm : movementsToCreate) {
                    ps.setInt(1, ingredientId);
                    ps.setDouble(2, sm.getValue().getQuantity());
                    ps.setString(3, sm.getValue().getUnit().name());
                    ps.setString(4, sm.getType().name());
                    ps.setTimestamp(5, Timestamp.from(Instant.now())); // création maintenant

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            sm.setId(rs.getInt("id"));
                            sm.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());
                            createdMovements.add(sm);
                        }
                    }
                }
                conn.commit();
                return createdMovements;
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error inserting stock movements", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
