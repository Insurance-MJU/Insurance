package infra.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AppConfig {

    private final ConfigLoader loader;

    public AppConfig(ConfigLoader loader) {
        this.loader = loader;
    }

    public DBConfig getDbConfig() {
        return new DBConfig(
            loader.get("db.url"),
            loader.get("db.username"),
            loader.get("db.password"),
            loader.getInt("db.pool.size", 10)
        );
    }

    public JwtConfig getJwtConfig() {
        return new JwtConfig(
            loader.get("jwt.secret"),
            loader.getInt("jwt.access.expiry.seconds", 3600),
            loader.getInt("jwt.refresh.expiry.seconds", 604800)
        );
    }

    public ServerConfig getServerConfig() {
        try {
            InetAddress host = InetAddress.getByName(loader.get("server.host", "127.0.0.1"));
            int port = loader.getInt("server.port", 8080);
            return new ServerConfig(host, port);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Invalid server.host in config", e);
        }
    }
}
