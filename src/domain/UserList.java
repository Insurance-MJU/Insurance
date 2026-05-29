package domain;

import common.exception.infra.BadRequestException;
import common.exception.infra.UnauthorizedException;
import domain.common.User;
import domain.common.UserRole;
import infra.dao.UserDao;

public class UserList {

    private final UserDao dao;

    public UserList(UserDao dao) {
        this.dao = dao;
    }

    public User login(String userId, String password) {
        User user = dao.findByCredentials(userId, password);
        if (user == null) throw new UnauthorizedException("아이디 또는 비밀번호가 올바르지 않습니다.");
        return user;
    }

    public User signup(String email, String name, String password) {
        if (dao.findById(email) != null)
            throw new BadRequestException("이미 사용 중인 이메일입니다.");
        dao.save(email, password, name, UserRole.CUSTOMER.name());
        return new User(email, password, name, UserRole.CUSTOMER);
    }
}
