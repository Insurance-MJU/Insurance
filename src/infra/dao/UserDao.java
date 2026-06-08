package infra.dao;

import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;
import infra.vo.UserVO;

public class UserDao {
    private final Database db;

    public UserDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<UserVO> EXTRACTOR = rs -> new UserVO(
        rs.getString("user_id"),
        rs.getString("password"),
        rs.getString("name"),
        rs.getString("role"),
        rs.getString("ssn")
    );

    public UserVO findByCredentials(String userId, String password) {
        UserVO vo = db.queryForObject(
            "SELECT * FROM users WHERE user_id = ?", EXTRACTOR, userId);
        if (vo != null && vo.password.equals(password)) return vo;
        return null;
    }

    public UserVO findById(String userId) {
        return db.queryForObject(
            "SELECT * FROM users WHERE user_id = ?", EXTRACTOR, userId);
    }

    public UserVO findBySsn(String ssn) {
        return db.queryForObject(
            "SELECT * FROM users WHERE ssn = ?", EXTRACTOR, ssn);
    }

    public void save(String userId, String password, String name, String role, String ssn) {
        db.execute(
            "INSERT INTO users (user_id, password, name, role, ssn) VALUES (?, ?, ?, ?, ?)",
            userId, password, name, role, ssn);
    }
}
