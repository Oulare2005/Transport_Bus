package uqac.dim.transportbus;

public class User {
    private String username;
    private String role;

    // Constructor
    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Getter for role
    public String getRole() {
        return role;
    }

    // Optional: Setter methods if needed
    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
