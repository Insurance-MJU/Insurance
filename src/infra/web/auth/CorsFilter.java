package infra.web.auth;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class CorsFilter {

    private static final String ALLOWED_ORIGIN = "http://localhost:3000";
    private static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS";
    private static final String ALLOWED_HEADERS = "Content-Type, Authorization";

    public boolean apply(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", ALLOWED_METHODS);
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", ALLOWED_HEADERS);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return true;
        }

        return false;
    }
}
