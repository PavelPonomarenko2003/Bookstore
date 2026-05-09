package repository.impl;

import entity.StockEntity;
import repository.interfaces.StockRepository;
import repository.mapper.impl.StockDbToObjectMapper;

import java.util.List;
import java.util.Optional;

public class StockRepositoryImpl extends JdbcBaseRepository<StockEntity, Long> implements StockRepository {

    private final StockDbToObjectMapper stockMapper = new StockDbToObjectMapper();

    @Override
    public void updateBooksQuantity(Long bookId, Integer quantity) {
        String sql = "INSERT INTO stock (book_id, quantity) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE quantity = VALUES(quantity)";

        executeUpdate(sql, bookId, quantity);
    }

    @Override
    public Integer getQuantity(Long bookId) {
        String sql = "SELECT quantity FROM stock WHERE book_id = ?";

        List<Integer> result = executeQuery(sql, rs -> rs.getInt("quantity"), bookId);

        return result.stream().findFirst().orElse(0);
    }

    @Override
    public boolean doWeHaveThatBooksInStock(Long bookId, Integer requestedQuantity) {
        return getQuantity(bookId) >= requestedQuantity;
    }

    @Override
    public void deleteBook(Long bookId) {
        String sql = "DELETE FROM stock WHERE book_id = ?";
        executeUpdate(sql, bookId);
    }

    @Override
    public void save(StockEntity entity) {
        updateBooksQuantity(entity.getId(), entity.getNumberOfBooksInStock());
    }

    @Override
    public Optional<StockEntity> findById(Long id) {
        String sql = "SELECT * FROM stock WHERE book_id = ?";
        return executeQuery(sql, stockMapper, id).stream().findFirst();
    }

    @Override
    public List<StockEntity> findAll() {
        String sql = "SELECT * FROM stock";
        return executeQuery(sql, stockMapper);
    }

    @Override
    public void delete(Long id) {
        deleteBook(id);
    }
}
