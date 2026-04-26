package entity;

/**
 * This abstract class is created that other classes can change and manage the id
 */

public abstract class BaseEntity {
    private Long id;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
