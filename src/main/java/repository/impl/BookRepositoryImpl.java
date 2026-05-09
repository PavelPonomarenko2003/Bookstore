package repository.impl;

import database.my_sql.DataSourceHikariConfiguration;
import entity.BookEntity;
import repository.interfaces.BookRepository;
import repository.mapper.impl.BookDbToObjectMapper;

import java.sql.*;
import java.util.List;
import java.util.Optional;

/**
 * That class is implementation of our interface Book repository,
 * that provide our db logic and simulate our db action (cause we don't use db on that stage)
 * Right now we have just basic functionality, but in future we can realize new methods
 * Other logic about method's acting and validation we'll make in service
  */
public class BookRepositoryImpl extends JdbcBaseRepository<BookEntity, Long>
        implements BookRepository {

    private final BookDbToObjectMapper bookMapper = new BookDbToObjectMapper();

    @Override
    public void save(BookEntity entity) {
        String sql = "INSERT INTO books (title, price) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE title = VALUES(title), price = VALUES(price)";

        Long generatedId = executeInsert(sql, entity.getTitle(), entity.getPrice());

        if (entity.getId() == null) {
            entity.setId(generatedId);
        }
    }

    @Override
    public Optional<BookEntity> findById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        return executeQuery(sql, bookMapper, id).stream().findFirst();
    }

    @Override
    public List<BookEntity> findAll() {
        String sql = "SELECT * FROM books";
        return executeQuery(sql, bookMapper);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        executeUpdate(sql, id);
    }

    @Override
    public Optional<BookEntity> findByTitle(String title) {
        String sql = "SELECT * FROM books WHERE LOWER(title) = LOWER(?)";
        return executeQuery(sql, bookMapper, title).stream().findFirst();
    }
}