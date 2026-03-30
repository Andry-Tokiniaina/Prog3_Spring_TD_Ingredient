package com.example.restservice.repositories;

import org.postgresql.util.PSQLException;

import com.example.restservice.DataSource;
import com.example.restservice.entities.Order;

import java.sql.*;

public class OrderRepository {
    DbUtils dbUtils = new DbUtils();
    Order findOrderByReference(String reference) {
        DataSource dbConnection = new DataSource();
        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                    select id, reference, creation_datetime from "order" where reference like ?""");
            preparedStatement.setString(1, reference);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Order order = new Order();
                Integer idOrder = resultSet.getInt("id");
                order.setId(idOrder);
                order.setReference(resultSet.getString("reference"));
                order.setCreationDatetime(resultSet.getTimestamp("creation_datetime").toInstant());
                /// setDishOrderList in OrderService
                return order;
            }
            throw new RuntimeException("Order not found with reference " + reference);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Order saveOrder(Order order) {
        String upsertOrderSql = """
                    INSERT INTO "order" (id, reference, creation_datetime)
                    VALUES (?, ?, ?)
                    ON CONFLICT (id) DO NOTHING
                    RETURNING id
                """;
        try (Connection conn = new DataSource().getConnection()) {
            conn.setAutoCommit(false);
            Integer orderId;
            try (PreparedStatement ps = conn.prepareStatement(upsertOrderSql)) {
                int nextSerialValue = dbUtils.getNextSerialValue(conn, "\"order\"", "id");
                if (order.getId() != null) {
                    ps.setInt(1, order.getId());
                } else {
                    ps.setInt(1, nextSerialValue);
                }
                ps.setString(2, order.getReference());
                ps.setTimestamp(3, Timestamp.from(order.getCreationDatetime()));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    } else {
                        orderId = order.getId() != null ? order.getId() : nextSerialValue;
                    }
                }
            }
            conn.commit();
            return findOrderByReference(order.getReference());
        } catch (PSQLException e) {
            if (e.getMessage().contains("duplicate key value violates unique constraint \"order_reference_unique\"")) {
                throw new RuntimeException("Order already exists with reference " + order.getReference());
            } else {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void detachOrders(Connection conn, Integer idOrder) {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM dish_order where id_order = ?")) {
            ps.setInt(1, idOrder);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
