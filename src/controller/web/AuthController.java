package controller.web;

import controller.web.dto.AuthResponse;
import controller.web.dto.LoginRequest;
import domain.UserList;
import domain.common.User;
import infra.web.Router;
import infra.web.auth.JwtUtil;

public class AuthController {

    private final UserList userList;
    private final JwtUtil jwtUtil;

    public AuthController(UserList userList, JwtUtil jwtUtil) {
        this.userList = userList;
        this.jwtUtil = jwtUtil;
    }

    public void registerRoutes(Router router) {
        router.post("/auth/login", (req, res) -> res.ok(login(req.body(LoginRequest.class))));
    }

    private AuthResponse login(LoginRequest req) {
        User user = userList.login(req.userId(), req.password());
        String token = jwtUtil.generateAccessToken(user.getUserId(), user.getRole().name());
        return new AuthResponse(token, user.getName(), user.getRole().name());
    }
}
