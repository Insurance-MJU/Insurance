package infra.config;

public record JwtConfig(String secret, int accessExpirySeconds, int refreshExpirySeconds) {}
