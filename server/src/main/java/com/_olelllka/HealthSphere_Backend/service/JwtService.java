package com._olelllka.HealthSphere_Backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt_secret}")
    @Setter
    private String key;

    public String generateJwt(String username) {
        return Jwts
                .builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hr
                .signWith(signingKey())
                .compact();
    }

    private Claims getClaims(String jwt) {
        return Jwts
                .parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public String extractUsername(String jwt) {
        return getClaims(jwt).getSubject();
    }

    public boolean isTokenValid(String jwt, String username) {
        Claims claims = getClaims(jwt);
        return claims.getSubject().equals(username) && claims.getExpiration().after(Date.from(Instant.now()));
    }

    private SecretKey signingKey() {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return Keys.hmacShaKeyFor(decodedKey);
    }

}
