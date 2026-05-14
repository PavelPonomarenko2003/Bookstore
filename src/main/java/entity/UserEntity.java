package entity;

import jakarta.persistence.*;

import java.io.Serial;

/**
 * Entity about our users
 * (for future logic)
 */

@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity{

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "username")
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public UserEntity(String username, Role role) {
        super();
        this.username = username;
        this.role = role;
    }

    public UserEntity() {
        super();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
