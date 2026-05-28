package infra.web.auth;

import common.exception.infra.UnauthorizedException;
import infra.config.JwtConfig;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    private final JwtConfig config;
    private final SecretKey signingKey;

    public JwtUtil(JwtConfig config) {
        this.config = config;
        this.signingKey = Keys.hmacShaKeyFor(config.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(int userId, String email) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + config.accessExpirySeconds() * 1000L))
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken(int userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + config.refreshExpirySeconds() * 1000L))
                .signWith(signingKey)
                .compact();
    }

    public void verify(String token) {
        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Token is expired.");
        } catch (JwtException e) {
            throw new UnauthorizedException("Invalid token.");
        }
    }
}
