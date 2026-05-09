package repository.mapper.impl;

import entity.OrderEntity;
import entity.OrderStatus;
import repository.mapper.interfaces.DbToObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class OrderDbToObjectMapper implements DbToObjectMapper<OrderEntity> {

    @Override
    public OrderEntity map(ResultSet set) throws SQLException {
        OrderEntity order = new OrderEntity();

        order.setId(set.getLong("id"));
        order.setUserId(set.getLong("user_id"));
        order.setTotalPrice(set.getBigDecimal("total_price"));

        String statusStr = set.getString("status");
        if (statusStr != null) {
            order.setOrderStatus(OrderStatus.valueOf(statusStr));
        }

        Timestamp timestamp = set.getTimestamp("created_at");
        if (timestamp != null) {
            order.setCreatedTimestamp(timestamp.toLocalDateTime());
        }

        order.setListBooksInOrder(new ArrayList<>());

        return order;
    }
}
