package entity;

import java.math.BigDecimal;

/**
 * Entity that provide descriptions about every book
 */

public class BookEntity extends BaseEntity{
    private String title;
    private BigDecimal price;

    public BookEntity(String title, BigDecimal price) {
        super();
        this.title = title;
        this.price = price;
    }

    public BookEntity() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
