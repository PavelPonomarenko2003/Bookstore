package repository.impl;

import database.my_sql.DataSourceHikariConfiguration;
import entity.OrderEntity;
import entity.OrderItemEntity;
import exception.sql_exception.DataStorageException;
import repository.interfaces.OrderRepository;
import repository.mapper.impl.OrderDbToObjectMapper;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class OrderRepositoryImpl extends JdbcBaseRepository<OrderEntity, Long> implements OrderRepository {

    private final OrderDbToObjectMapper orderMapper = new OrderDbToObjectMapper();

    @Override
    public List<OrderEntity> findAllByUserId(Long userId) {
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        List<OrderEntity> orders = executeQuery(sql, orderMapper, userId);

        orders.forEach(this::loadOrderItems);
        return orders;
    }

    @Override
    public Optional<OrderEntity> findById(Long id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        Optional<OrderEntity> order = executeQuery(sql, orderMapper, id).stream().findFirst();

        order.ifPresent(this::loadOrderItems);
        return order;
    }

    @Override
    public List<OrderEntity> findAll() {
        List<OrderEntity> orders = executeQuery("SELECT * FROM orders", orderMapper);
        orders.forEach(this::loadOrderItems);
        return orders;
    }

    @Override
    public void delete(Long id) {
        executeUpdate("DELETE FROM order_items WHERE order_id = ?", id);
        executeUpdate("DELETE FROM orders WHERE id = ?", id);
    }

    private void loadOrderItems(OrderEntity order) {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        List<OrderItemEntity> items = executeQuery(sql, rs -> {
            OrderItemEntity item = new OrderItemEntity();
            item.setBookId(rs.getLong("book_id"));
            item.setNumberOfBooks(rs.getInt("quantity"));
            item.setPriceAtTheTimeOfPurchase(rs.getBigDecimal("price_at_purchase"));
            return item;
        }, order.getId());

        order.setListBooksInOrder(items);
    }

    @Override
    public void save(OrderEntity entity) {

        String insertOrderSql = "INSERT INTO orders (id, user_id, total_price, status, created_at, finished_at) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE status = VALUES(status), finished_at = VALUES(finished_at)";

        String insertItemSql = "INSERT IGNORE INTO order_items (order_id, book_id, quantity, price_at_purchase)" +
                " VALUES (?, ?, ?, ?)";

        try (Connection conn = DataSourceHikariConfiguration.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psOrder = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                    psOrder.setObject(1, entity.getId());
                    psOrder.setLong(2, entity.getUserId());
                    psOrder.setBigDecimal(3, entity.getTotalPrice());
                    psOrder.setString(4, entity.getOrderStatus().name());
                    psOrder.setTimestamp(5, Timestamp.valueOf(entity.getCreatedTimestamp()));

                    psOrder.setTimestamp(6, entity.getFinishedTimestamp() != null ?
                            Timestamp.valueOf(entity.getFinishedTimestamp()) : null);

                    psOrder.executeUpdate();

                    if (entity.getId() == null) {
                        try (ResultSet keys = psOrder.getGeneratedKeys()) {
                            if (keys.next()) {
                                entity.setId(keys.getLong(1));
                            }
                        }
                    }
                }

                if (entity.getListBooksInOrder() != null && !entity.getListBooksInOrder().isEmpty()) {
                    try (PreparedStatement psItem = conn.prepareStatement(insertItemSql)) {
                        for (OrderItemEntity item : entity.getListBooksInOrder()) {
                            psItem.setLong(1, entity.getId());
                            psItem.setLong(2, item.getBookId());
                            psItem.setInt(3, item.getNumberOfBooks());
                            psItem.setBigDecimal(4, item.getPriceAtTheTimeOfPurchase());
                            psItem.addBatch();
                        }
                        psItem.executeBatch();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new DataStorageException("Failed to save order", e);
            }
        } catch (SQLException e) {
            throw new DataStorageException("Connection error", e);
        }
    }
}