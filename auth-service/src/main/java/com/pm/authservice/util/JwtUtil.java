package com.pm.authservice.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;


@Slf4j
@Component
public class JwtUtil {
    // Utility for creating, signing and validating JSON Web Tokens (JWTs): provides token generation, claim extraction (e.g. subject, roles), expiration checks, token refreshing, and signature/secret management.
    private final Key secretKey;

    //constructor injection
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        /// converting a string secret into a Key object suitable for signing JWTs using HMAC SHA algorithms.
        byte[] keyBytes = Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8));

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        log.info("JWT_SECRET raw value: {}", secret);
        log.info("JWT_SECRET length: {}", secret.length());

    }

    /// Generates a JWT token containing the user's email and role as claims, with a set expiration time.
    public String generateToken(String email, String role){
        return Jwts.builder()
                .subject(email)
                .claim("role" ,role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 10000 * 60 * 60 * 10)) //100 hours
                .signWith(secretKey)
                .compact();

    }


    /// Validates the provided JWT token's signature and structure, throwing exceptions for invalid tokens.
    public void validateToken(String token) {
        try {
            Jwts.parser().
                    verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token);
            log.info("Token validated successfully");
        }
        catch (SignatureException e) {
            throw new JwtException("Invalid JWT signature");
        }
        catch (JwtException e){
            throw new JwtException("Invalid JWT token");
        }
    }
}
