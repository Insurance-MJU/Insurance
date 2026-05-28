package infra.web;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router {

    private final List<Route> routes = new ArrayList<>();

    public void get(String path, RouteHandler handler)    { routes.add(new Route("GET",    path, handler)); }
    public void post(String path, RouteHandler handler)   { routes.add(new Route("POST",   path, handler)); }
    public void put(String path, RouteHandler handler)    { routes.add(new Route("PUT",    path, handler)); }
    public void delete(String path, RouteHandler handler) { routes.add(new Route("DELETE", path, handler)); }

    public RouteMatch resolve(String method, String path) {
        for (Route route : routes) {
            Map<String, String> vars = route.match(method, path);
            if (vars != null) return new RouteMatch(route.handler, vars);
        }
        return null;
    }

    private static class Route {
        private final String method;
        private final Pattern pattern;
        private final List<String> paramNames;
        private final RouteHandler handler;

        Route(String method, String pathPattern, RouteHandler handler) {
            this.method = method;
            this.handler = handler;
            this.paramNames = new ArrayList<>();

            Matcher m = Pattern.compile("\\{([^}]+)\\}").matcher(pathPattern);
            while (m.find()) paramNames.add(m.group(1));

            String regex = pathPattern.replaceAll("\\{[^}]+\\}", "([^/]+)");
            this.pattern = Pattern.compile("^" + regex + "$");
        }

        Map<String, String> match(String method, String path) {
            if (!this.method.equalsIgnoreCase(method)) return null;
            Matcher m = pattern.matcher(path);
            if (!m.matches()) return null;

            Map<String, String> vars = new HashMap<>();
            for (int i = 0; i < paramNames.size(); i++) {
                vars.put(paramNames.get(i), m.group(i + 1));
            }
            return vars;
        }
    }

    public static class RouteMatch {
        public final RouteHandler handler;
        public final Map<String, String> pathVariables;

        RouteMatch(RouteHandler handler, Map<String, String> pathVariables) {
            this.handler = handler;
            this.pathVariables = pathVariables;
        }
    }
}
