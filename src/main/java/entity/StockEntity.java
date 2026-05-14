package entity;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * Entity that will show us what product we have in the stock
 */

@Entity
@Table(name = "stock")
public class StockEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @OneToOne
    @MapsId // notify that id from BaseEntity that is id of certain book
    @JoinColumn(name = "book_id") // name in table stock, that os PK and FK as the same time
    private BookEntity book;

    @Column(name = "quantity")
    private Integer numberOfBooksInStock;

    public StockEntity(BookEntity book, Integer numberOfBooksInStock) {
        this.book = book;
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