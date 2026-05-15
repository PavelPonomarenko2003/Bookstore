package bookstore.dto;

import bookstore.entity.BookEntity;
import bookstore.entity.OrderEntity;
import bookstore.entity.StockEntity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * BookstoreStorageForSerializingDTO using like main storage of our bookstore for all data transfer
 */
public class BookstoreStorageForSerializingDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<BookEntity> listOfBooks;
    private final List<OrderEntity> listOfOrders;
    private final List<StockEntity> listOfStock;

    public BookstoreStorageForSerializingDTO(List<BookEntity> listOfBooks,
                                             List<OrderEntity> listOfOrders,
                                             List<StockEntity> listOfStock) {
        this.listOfBooks = listOfBooks;
        this.listOfOrders = listOfOrders;
        this.listOfStock = listOfStock;
    }

    // getters so we can recover our data
    public List<BookEntity> getListOfBooks() {
        return listOfBooks;
    }

    public List<OrderEntity> getListOfOrders() {
        return listOfOrders;
    }

    public List<StockEntity> getListOfStock() {
        return listOfStock;
    }
}
