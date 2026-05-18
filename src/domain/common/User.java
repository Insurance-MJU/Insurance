package domain.common;

public class User {
    private final String userId;
    private final String password;
    private final String name;
    private final UserRole role;

    public User(String userId, String password, String name, UserRole role) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public UserRole getRole() { return role; }

    // ── DAO 위임 ──────────────────────────────────────────────
    public static User findByCredentials(String userId, String password) {
        return infra.dao.UserDao.getInstance().findByCredentials(userId, password);
    }
}
