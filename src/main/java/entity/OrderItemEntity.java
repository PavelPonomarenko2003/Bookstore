package entity;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entity that will provide short description about our purchase
 * Composite PK
 */
@Entity
@Table(name = "order_items")
@IdClass(OrderItemEntity.OrderItemId.class)
public class OrderItemEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private BookEntity book;

    @Column(name = "quantity")
    private Integer numberOfBooks;

    @Column(name = "price_at_purchase")
    private BigDecimal priceAtTheTimeOfPurchase;

    public OrderItemEntity() {
    }

    public OrderItemEntity(BookEntity book, Integer numberOfBooks, BigDecimal priceAtTheTimeOfPurchase) {
        this.book = book;
        this.numberOfBooks = numberOfBooks;
        this.priceAtTheTimeOfPurchase = priceAtTheTimeOfPurchase;
    }

    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }

    public BookEntity getBook() { return book; }
    public void setBook(BookEntity book) { this.book = book; }

    public Integer getNumberOfBooks() { return numberOfBooks; }
    public void setNumberOfBooks(Integer numberOfBooks) { this.numberOfBooks = numberOfBooks; }

    public BigDecimal getPriceAtTheTimeOfPurchase() { return priceAtTheTimeOfPurchase; }
    public void setPriceAtTheTimeOfPurchase(BigDecimal priceAtTheTimeOfPurchase) { this.priceAtTheTimeOfPurchase = priceAtTheTimeOfPurchase; }

    public static class OrderItemId implements Serializable {
        private Long order;
        private Long book;

        public OrderItemId() {}

        public OrderItemId(Long order, Long book) {
            this.order = order;
            this.book = book;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OrderItemId that = (OrderItemId) o;
            return Objects.equals(order, that.order) && Objects.equals(book, that.book);
        }

        @Override
        public int hashCode() {
            return Objects.hash(order, book);
        }
    }
}
