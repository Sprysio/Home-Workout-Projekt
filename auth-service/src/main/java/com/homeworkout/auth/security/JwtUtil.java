package com.homeworkout.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.homeworkout.auth.model.Role;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, Set<Role> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        List<String> roleNames = roles.stream()
                .map(Role::name)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(username)
                .claim("roles", roleNames)
                .issuedAt(now)
                .expiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Set<String> getRolesFromToken(String token) {
        Object raw = extractAllClaims(token).get("roles");

        if (raw instanceof List<?>) {
            return ((List<?>) raw).stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }

        return Set.of();
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}