package infra.web.auth;

import java.util.Set;

public class WhiteList {

    private static final Set<String> PATHS = Set.of(
            "/auth/login",
            "/auth/signup"
    );

    public static boolean contains(String path) {
        return PATHS.contains(path);
    }
}
