package infra.web.auth;

import java.util.Set;

public class WhiteList {

    private static final Set<String> EXACT = Set.of(
            "/auth/login",
            "/auth/signup"
    );

    public static boolean contains(String path) {
        if (EXACT.contains(path)) return true;
        if (path.startsWith("/public/")) return true;
        if (path.startsWith("/verification/")) return true;
        return false;
    }
}
