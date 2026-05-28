package domain;

import common.exception.infra.UnauthorizedException;
import domain.common.User;
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
}
