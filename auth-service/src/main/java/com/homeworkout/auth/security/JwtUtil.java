// java
     package com.homeworkout.auth.security;

     import io.jsonwebtoken.*;
     import io.jsonwebtoken.security.Keys;
     import org.springframework.beans.factory.annotation.Value;
     import org.springframework.stereotype.Component;

     import java.nio.charset.StandardCharsets;
     import java.security.Key;
     import java.util.Date;

     @Component
     public class JwtUtil {
         @Value("${jwt.secret}")
         private String secret;

         @Value("${jwt.expiration-ms}")
         private long expirationMs;

         private Key getSigningKey() {
             return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
         }

         public String generateToken(String username) {
             Date now = new Date();
             Date exp = new Date(now.getTime() + expirationMs);
             return Jwts.builder()
                     .setSubject(username)
                     .setIssuedAt(now)
                     .setExpiration(exp)
                     .signWith(getSigningKey())
                     .compact();
         }

         public String getUsernameFromToken(String token) {
             return Jwts.parser()
                     .setSigningKey(getSigningKey())
                     .build()
                     .parseClaimsJws(token)
                     .getBody()
                     .getSubject();
         }

         public boolean validateToken(String token) {
             try {
                 Jwts.parser()
                         .setSigningKey(getSigningKey())
                         .build()
                         .parseClaimsJws(token);
                 return true;
             } catch (JwtException | IllegalArgumentException ex) {
                 return false;
             }
         }
     }