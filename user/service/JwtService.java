package com.Syncast.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "mysecretkeymysecretkeymysecretkey12345";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(UserDetails userDetails) {

        return Jwts.builder()

                .setSubject(userDetails.getUsername())

                .setIssuedAt(new Date())

                .setExpiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)
                )

                .signWith(getSigningKey(), SignatureAlgorithm.HS256)

                .compact();
    }

    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()

                .setSigningKey(getSigningKey())

                .build()

                .parseClaimsJws(token)

                .getBody();
    }

    public boolean isTokenValid(String token,
                                UserDetails userDetails) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername());
    }
}