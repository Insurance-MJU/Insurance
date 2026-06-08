package domain;

import common.exception.infra.BadRequestException;
import common.exception.infra.UnauthorizedException;
import domain.common.User;
import domain.common.UserRole;
import infra.dao.UserDao;
import infra.vo.UserVO;

public class UserList {

    private final UserDao dao;

    public UserList(UserDao dao) {
        this.dao = dao;
    }

    private static User toDomain(UserVO vo) {
        if (vo == null) return null;
        return new User(vo.userId, vo.password, vo.name, UserRole.valueOf(vo.role), vo.ssn);
    }

    public User login(String userId, String password) {
        User user = toDomain(dao.findByCredentials(userId, password));
        if (user == null) throw new UnauthorizedException("아이디 또는 비밀번호가 올바르지 않습니다.");
        return user;
    }

    public User signup(String email, String name, String password, String ssn) {
        if (dao.findById(email) != null)
            throw new BadRequestException("이미 사용 중인 이메일입니다.");
        dao.save(email, password, name, UserRole.CUSTOMER.name(), ssn);
        return new User(email, password, name, UserRole.CUSTOMER, ssn);
    }

    public User findOrCreateBySsn(String ssn, String name) {
        User existing = toDomain(dao.findBySsn(ssn));
        if (existing != null) return existing;
        String userId = "IDENTITY-" + ssn.replaceAll("[^0-9]", "").substring(0, Math.min(10, ssn.replaceAll("[^0-9]", "").length()));
        dao.save(userId, "", name, UserRole.CUSTOMER.name(), ssn);
        return new User(userId, "", name, UserRole.CUSTOMER, ssn);
    }
}
