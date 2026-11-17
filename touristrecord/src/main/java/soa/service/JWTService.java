package soa.service;

import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

    private final SecretKey key;
    private final long expirationMs;

    public JWTService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8)));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, String role) {

        return Jwts.builder()
                .subject(username)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, Jwts.SIG.HS384)
                .compact();
    }

    public Claims validateToken(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public SecretKey getKey() {
        return key;
    }
}
