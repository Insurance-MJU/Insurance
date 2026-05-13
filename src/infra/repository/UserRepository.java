package infra.repository;

import domain.common.User;
import domain.common.UserRole;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static final Map<String, User> USERS = new HashMap<>();

    static {
        USERS.put("customer1", new User("customer1", "1234", "박수현", UserRole.CUSTOMER));
        USERS.put("employee1", new User("employee1", "1234", "김직원", UserRole.EMPLOYEE));
        USERS.put("admin1",    new User("admin1",    "1234", "이관리", UserRole.ADMIN));
    }

    public User findByCredentials(String userId, String password) {
        User user = USERS.get(userId);
        if (user != null && user.getPassword().equals(password)) return user;
        return null;
    }
}
