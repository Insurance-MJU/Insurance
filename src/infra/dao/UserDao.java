package infra.dao;

import domain.common.User;
import domain.common.UserRole;
import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;

public class UserDao {
    private final Database db;

    public UserDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<User> EXTRACTOR = rs -> new User(
        rs.getString("user_id"),
        rs.getString("password"),
        rs.getString("name"),
        UserRole.valueOf(rs.getString("role"))
    );

    public User findByCredentials(String userId, String password) {
        User user = db.queryForObject(
            "SELECT * FROM users WHERE user_id = ?", EXTRACTOR, userId);
        if (user != null && user.getPassword().equals(password)) return user;
        return null;
    }

    public User findById(String userId) {
        return db.queryForObject(
            "SELECT * FROM users WHERE user_id = ?", EXTRACTOR, userId);
    }

    public void save(String userId, String password, String name, String role) {
        db.execute(
            "INSERT INTO users (user_id, password, name, role) VALUES (?, ?, ?, ?)",
            userId, password, name, role);
    }
}
