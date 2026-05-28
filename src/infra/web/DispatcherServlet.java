package infra.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import common.exception.dao.DataAccessException;
import common.exception.domain.DomainException;
import common.exception.infra.InfraException;
import infra.web.auth.JwtFilter;
import infra.web.dto.HttpRequest;
import infra.web.dto.HttpResponse;

import java.io.IOException;

public class DispatcherServlet implements HttpHandler {

    private final Router router;
    private final JwtFilter jwtFilter;

    public DispatcherServlet(Router router, JwtFilter jwtFilter) {
        this.router = router;
        this.jwtFilter = jwtFilter;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:3000");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        String method = exchange.getRequestMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();

        HttpResponse response = new HttpResponse(exchange);
        Router.RouteMatch match = router.resolve(method, path);

        if (match == null) {
            response.error(404, "URL Not Found");
            return;
        }
        HttpRequest request = new HttpRequest(exchange, match.pathVariables);

        try {
            jwtFilter.verify(request);
            match.handler.handle(request, response);
        } catch (DomainException e) {
            System.err.println("[DOMAIN] " + e.getMessage());
            response.error(e.getStatus(), e.getMessage());
        } catch (DataAccessException e) {
            System.err.println("[DAO] " + e.getMessage());
            response.error(e.getStatus(), e.getMessage());
        } catch (InfraException e) {
            System.err.println("[INFRA] " + e.getMessage());
            response.error(e.getStatus(), e.getMessage());
        } catch (Exception e) {
            System.err.println("[GLOBAL] " + e.getMessage());
            response.error(500, "Internal Server Error");
        }
    }
}
