package entity;

import java.math.BigDecimal;

/**
 * Entity that will provide short description about our purchase
 * (We will save it into our order like an additional description)
 */

public class OrderItemEntity{
    private Long bookId;
    private Integer numberOfBooks;
    private BigDecimal priceAtTheTimeOfPurchase;

    public OrderItemEntity(
            Long bookId,
            Integer numberOfBooks,
            BigDecimal priceAtTheTimeOfPurchase
    ) {
        this.bookId = bookId;
        this.numberOfBooks = numberOfBooks;
        this.priceAtTheTimeOfPurchase = priceAtTheTimeOfPurchase;
    }

    public OrderItemEntity() {
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Integer getNumberOfBooks() {
        return numberOfBooks;
    }

    public void setNumberOfBooks(Integer numberOfBooks) {
        this.numberOfBooks = numberOfBooks;
    }

    public BigDecimal getPriceAtTheTimeOfPurchase() {
        return priceAtTheTimeOfPurchase;
    }

    public void setPriceAtTheTimeOfPurchase(BigDecimal priceAtTheTimeOfPurchase) {
        this.priceAtTheTimeOfPurchase = priceAtTheTimeOfPurchase;
    }
}
