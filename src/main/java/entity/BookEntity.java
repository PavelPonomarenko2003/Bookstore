package entity;

import java.awt.print.Book;
import java.io.Serial;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entity that provide descriptions about every book
 */

public class BookEntity extends BaseEntity{

    @Serial
    private static final long serialVersionUID = 1L;

    private String title;
    private BigDecimal price;

    public BookEntity(Long id, String title, BigDecimal price) {
        super();
        this.setId(id);
        this.title = title;
        this.price = price;
    }


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

    @Override
    public boolean equals(Object o){
        // links comparing
        if(this == o) return true;
        // if links equals null or type of our objects are different -> go for false
        if(o == null || getClass() != o.getClass()) return false;

        // We make that transformation when we are sure that this object is definitely a book
        BookEntity that = (BookEntity) o;

        if (this.getId() != null && that.getId() != null) {
            return this.getId().equals(that.getId());
        }
        // on case if that is new book and wasn't in catalog
        return Objects.equals(title, that.title);
    }

    @Override
    public int hashCode(){
        // if we have an id -> using id, if we haven't using title
        if(getId() != null) return Objects.hash(getId());
        else return Objects.hash(title);
    }

}
