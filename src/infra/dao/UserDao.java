package infra.dao;

import domain.common.User;
import domain.common.UserRole;
import java.util.*;

public class UserDao {
    private static final UserDao INSTANCE = new UserDao();
    public static UserDao getInstance() { return INSTANCE; }

    private static final Map<String, User> STORE = new HashMap<>();
    static {
        STORE.put("customer1", new User("customer1", "1234", "박수현", UserRole.CUSTOMER));
        STORE.put("employee1", new User("employee1", "1234", "김직원", UserRole.EMPLOYEE));
        STORE.put("admin1",    new User("admin1",    "1234", "이관리", UserRole.ADMIN));
    }

    public User findByCredentials(String userId, String password) {
        User user = STORE.get(userId);
        if (user != null && user.getPassword().equals(password)) return user;
        return null;
    }
}
