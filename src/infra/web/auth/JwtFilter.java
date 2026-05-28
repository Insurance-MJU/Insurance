package infra.web.auth;

import common.exception.infra.UnauthorizedException;
import infra.web.dto.HttpRequest;

public class JwtFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public void verify(HttpRequest req) {
        if (WhiteList.contains(req.path())) return;

        String header = req.header("Authorization");
        if (header == null || !header.startsWith("Bearer "))
            throw new UnauthorizedException("No Token.");

        jwtUtil.verify(header.substring(7));
    }
}
