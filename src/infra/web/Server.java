package infra.web;

import com.sun.net.httpserver.HttpServer;
import infra.config.ServerConfig;

import java.io.IOException;
import java.util.concurrent.Executors;

public class Server {

    private final HttpServer httpServer;
    private final ServerConfig config;

    public Server(ServerConfig config) {
        this.config = config;
        try {
            this.httpServer = HttpServer.create(config.getBindAddress(), 0);
            this.httpServer.setExecutor(Executors.newFixedThreadPool(5));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create HTTP server", e);
        }
    }

    public void start(DispatcherServlet dispatcher) {
        httpServer.createContext("/", dispatcher);
        httpServer.start();
        System.out.println("[Server] Listening on " + config.getBindAddress());
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("[Server] Stopped");
    }
}
