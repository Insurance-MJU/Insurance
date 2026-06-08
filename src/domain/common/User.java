package domain.common;

public class User {
    private final String userId;
    private final String password;
    private final String name;
    private final UserRole role;
    private final String ssn;

    public User(String userId, String password, String name, UserRole role) {
        this(userId, password, name, role, null);
    }

    public User(String userId, String password, String name, UserRole role, String ssn) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.role = role;
        this.ssn = ssn;
    }

    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public UserRole getRole() { return role; }
    public String getSsn() { return ssn; }
}
