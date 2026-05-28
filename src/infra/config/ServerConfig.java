package infra.config;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ServerConfig {
    private final InetSocketAddress bindAddress;

    public ServerConfig(InetAddress host, int port) {
        this.bindAddress = new InetSocketAddress(host, port);
    }

    public InetSocketAddress getBindAddress() { return bindAddress; }
}
