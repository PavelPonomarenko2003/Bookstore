package repository.mapper.impl;

import entity.StockEntity;
import repository.mapper.interfaces.DbToObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StockDbToObjectMapper implements DbToObjectMapper<StockEntity> {

    public StockDbToObjectMapper() {
    }

    @Override
    public StockEntity map(ResultSet rs) throws SQLException {

        Long bookId = rs.getLong("book_id");
        Integer quantity = rs.getInt("quantity");

        return new StockEntity(bookId, quantity);
    }
}
