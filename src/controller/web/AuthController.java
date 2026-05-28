package controller.web;

import common.exception.infra.UnauthorizedException;
import controller.web.dto.AuthResponse;
import controller.web.dto.LoginRequest;
import domain.common.User;
import infra.dao.UserDao;
import infra.web.Router;
import infra.web.auth.JwtUtil;

public class AuthController {

    private final UserDao userDao;
    private final JwtUtil jwtUtil;

    public AuthController(UserDao userDao, JwtUtil jwtUtil) {
        this.userDao = userDao;
        this.jwtUtil = jwtUtil;
    }

    public void registerRoutes(Router router) {
        router.post("/auth/login", (req, res) -> {
            LoginRequest body = req.body(LoginRequest.class);
            User user = userDao.findByCredentials(body.userId(), body.password());
            if (user == null) throw new UnauthorizedException("아이디 또는 비밀번호가 올바르지 않습니다.");
            String token = jwtUtil.generateAccessToken(user.getUserId().hashCode(), user.getUserId());
            res.ok(new AuthResponse(token, user.getName(), user.getRole().name()));
        });
    }
}
