package infra.web;

import infra.web.dto.HttpRequest;
import infra.web.dto.HttpResponse;

@FunctionalInterface
public interface RouteHandler {
    void handle(HttpRequest request, HttpResponse response) throws Exception;
}
