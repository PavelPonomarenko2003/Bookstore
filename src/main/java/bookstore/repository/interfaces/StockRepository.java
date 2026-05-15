package bookstore.repository.interfaces;

import bookstore.entity.StockEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE stock SET quantity = :quantity WHERE book_id = :bookId", nativeQuery = true)
    void updateBooksQuantity(@Param("bookId") Long bookId, @Param("quantity") Integer quantity);

    @Query(value = "SELECT quantity FROM stock WHERE book_id = :bookId", nativeQuery = true)
    Optional<Integer> findQuantityByBookId(@Param("bookId") Long bookId);

    // self-interface realization
    default Integer getQuantity(Long bookId) {
        return findQuantityByBookId(bookId).orElse(0);
    }

    default boolean doWeHaveThatBooksInStock(Long bookId, Integer requestedAmount) {
        return getQuantity(bookId) >= requestedAmount;
    }

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM stock WHERE book_id = :bookId", nativeQuery = true)
    void deleteBook(@Param("bookId") Long bookId);
}
