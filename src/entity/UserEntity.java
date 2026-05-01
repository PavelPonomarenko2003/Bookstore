package entity;

/**
 * Entity about our users
 * (for future logic)
 */

public class UserEntity extends BaseEntity{
    private String username;
    private Role role;

    public UserEntity(String username, Role role) {
        super();
        this.username = username;
        this.role = role;
    }

    public UserEntity() {
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
