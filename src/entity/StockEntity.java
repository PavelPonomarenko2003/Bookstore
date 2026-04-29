package entity;

/**
 * Entity that will show us what product we have in the stock
 */

public class StockEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    private Integer numberOfBooksInStock;

    public StockEntity(Long bookId, Integer numberOfBooksInStock) {
        super();
        this.numberOfBooksInStock = numberOfBooksInStock;
    }

    public StockEntity() {
    }

    public Integer getNumberOfBooksInStock() {
        return numberOfBooksInStock;
    }

    public void setNumberOfBooksInStock(Integer numberOfBooksInStock) {
        this.numberOfBooksInStock = numberOfBooksInStock;
    }
}
