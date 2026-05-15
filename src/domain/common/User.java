package domain.common;

import java.util.HashMap;
import java.util.Map;

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

    // ── 영속성 ────────────────────────────────────────────────
    private static final Map<String, User> STORE = new HashMap<>();
    static {
        STORE.put("customer1", new User("customer1", "1234", "박수현", UserRole.CUSTOMER));
        STORE.put("employee1", new User("employee1", "1234", "김직원", UserRole.EMPLOYEE));
        STORE.put("admin1",    new User("admin1",    "1234", "이관리", UserRole.ADMIN));
    }

    public static User findByCredentials(String userId, String password) {
        User user = STORE.get(userId);
        if (user != null && user.password.equals(password)) return user;
        return null;
    }
}
