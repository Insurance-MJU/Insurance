package infra.web.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

public class HttpRequest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpExchange exchange;
    private final Map<String, String> pathVariables;

    public HttpRequest(HttpExchange exchange, Map<String, String> pathVariables) {
        this.exchange = exchange;
        this.pathVariables = pathVariables;
    }

    public String method() {
        return exchange.getRequestMethod();
    }

    public String header(String name) {
        return exchange.getRequestHeaders().getFirst(name);
    }

    public String path() {
        return exchange.getRequestURI().getPath();
    }

    public String pathVariable(String name) {
        return pathVariables.get(name);
    }

    public int intPath(String name) {
        return Integer.parseInt(pathVariables.get(name));
    }

    public String queryParam(String name) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2 && kv[0].equals(name)) return kv[1];
        }
        return null;
    }

    public String body() throws IOException {
        return new String(exchange.getRequestBody().readAllBytes());
    }

    public <T> T body(Class<T> type) throws IOException {
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        if (bytes.length == 0) throw new ValidationException("No Request Body");
        try {
            return MAPPER.readValue(bytes, type);
        } catch (JsonProcessingException e) {
            throw new ValidationException("Invalid Request Body");
        }
    }
}
