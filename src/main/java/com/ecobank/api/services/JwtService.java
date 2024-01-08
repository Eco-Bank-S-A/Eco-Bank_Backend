package com.ecobank.api.services;

import com.ecobank.api.database.entities.User;
import com.ecobank.api.models.jwt.JwtTokenData;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public String generateToken(User user) {
        var date = LocalDateTime.now().plusMinutes(20);
        var expirationDate = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());

        return Jwts
            .builder()
            .subject(user.getEmail())
            .issuedAt(new Date())
            .expiration(expirationDate)
            .signWith(getSignInKey())
            .compact();
    }

    public JwtTokenData verifyToken(String token) {
        try {
            var payload = Jwts
                    .parser()
                    .verifyWith((SecretKey) getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            System.out.println("Dobry token");
            return JwtTokenData
                .builder()
                .token(token)
                .isVerified(true)
                .userEmail(payload.getSubject())
                .issuedAt(payload.getIssuedAt())
                .expiration(payload.getExpiration())
                .build();

        } catch (JwtException e) {
            return JwtTokenData
                .builder()
                .token(token)
                .isVerified(false)
                .build();
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
