package com.example.restservice.repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.restservice.DataSource;
import com.example.restservice.entities.Dish;
import com.example.restservice.entities.enums.DishTypeEnum;
import com.example.restservice.entities.DishIngredient;
import org.springframework.stereotype.Component;

@Component
public class DishRepository {
    DbUtils dbUtils = new DbUtils();

    public List<Dish> findDish(){
        DataSource dataSource = new DataSource();
        List<Dish> dishList = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()){
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT id, name, category, price FROM Dish");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("id"));
                dish.setName(resultSet.getString("name"));
                dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("category")));
                dish.setPrice(resultSet.getDouble("price"));
                dishList.add(dish);
            }
            return dishList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dish findDishById(Integer id) throws SQLException {
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            select dish.id as dish_id, dish.name as dish_name, dish_type, dish.selling_price as dish_price
                            from dish
                            where dish.id = ?;
                            """);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));
                dish.setPrice(resultSet.getObject("dish_price") == null
                        ? null : resultSet.getDouble("dish_price"));
                return dish;
            }
            ds.closeConnection(connection);
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dish saveDish(Dish toSave) {
        String upsertDishSql = """
                    INSERT INTO dish (id, selling_price, name, dish_type)
                    VALUES (?, ?, ?, ?::dish_type)
                    ON CONFLICT (id) DO UPDATE
                    SET name = EXCLUDED.name,
                        dish_type = EXCLUDED.dish_type,
                        selling_price = EXCLUDED.selling_price
                    RETURNING id
                """;

        try (Connection conn = new DataSource().getConnection()) {
            conn.setAutoCommit(false);
            Integer dishId;
            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                if (toSave.getId() != null) {
                    ps.setInt(1, toSave.getId());
                } else {
                    ps.setInt(1, dbUtils.getNextSerialValue(conn, "dish", "id"));
                }
                if (toSave.getPrice() != null) {
                    ps.setDouble(2, toSave.getPrice());
                } else {
                    ps.setNull(2, Types.DOUBLE);
                }
                ps.setString(3, toSave.getName());
                ps.setString(4, toSave.getDishType().name());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    dishId = rs.getInt(1);
                }
            }
            conn.commit();
            return findDishById(dishId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void detachIngredients (int dishId) {
        DataSource dataSource = new DataSource();
        String deleteSql = "DELETE FROM dish_ingredient WHERE id_dish = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(deleteSql)) {
            stmt.setInt(1, dishId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void attachIngredients (int dishId, List<Integer> ingIds) {
        DataSource dataSource = new DataSource();
        String insertSql = """
           insert into dish_ingredient (id_ingredient,id_dish, required_quantity, unit)
           values (?, ?, 1, 'KG')
        """;

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                for (Integer ingId : ingIds) {
                    stmt.setInt(1, ingId);
                    stmt.setInt(2, dishId);
                    stmt.addBatch();
                }
                stmt.executeBatch();
                connection.commit();
            }
            catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
