package repository.mapper.impl;

import entity.BookEntity;
import repository.mapper.interfaces.DbToObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper for books
 */
public class BookDbToObjectMapper implements DbToObjectMapper<BookEntity> {

    public BookEntity map(ResultSet resultSet) throws SQLException {
        BookEntity book = new BookEntity();

        book.setId(resultSet.getLong("id"));
        book.setTitle(resultSet.getString("title"));
        book.setPrice(resultSet.getBigDecimal("price"));

        return book;
    }

}
