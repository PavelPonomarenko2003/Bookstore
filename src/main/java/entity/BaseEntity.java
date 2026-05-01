package entity;

import java.io.Serial;
import java.io.Serializable;

/**
 * This abstract class is created that other classes can change and manage the id
 */

public abstract class BaseEntity implements Serializable {
    // Now we can change and modify our class data
    // and it'll works and use serialization correctly
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    public BaseEntity(Long id) {
        this.id = id;
    }

    public BaseEntity() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
