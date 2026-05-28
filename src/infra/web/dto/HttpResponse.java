package infra.web.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpExchange exchange;
    private boolean committed = false;

    public HttpResponse(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public void ok(Object body) throws IOException      { send(200, MAPPER.writeValueAsString(body instanceof List<?> ? Map.of("data", body) : body)); }
    public void created(Object body) throws IOException { send(201, MAPPER.writeValueAsString(body)); }
    public void noContent() throws IOException          { send(204, ""); }
    public void error(int status, String message) throws IOException { send(status, MAPPER.writeValueAsString(new ErrorBody(status, message))); }

    public void send(int status, String body) throws IOException {
        if (committed) return;
        committed = true;

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);

        if (bytes.length > 0) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } else {
            exchange.getResponseBody().close();
        }
    }
}
