package com.codecluster.auth.security;

import com.codecluster.auth.dto.GenerateJwtDto;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

import io.jsonwebtoken.security.Keys;

import java.util.Date;

import io.jsonwebtoken.Jwts;

import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;


@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(GenerateJwtDto dto) {

        Date now = new Date();

        Date expiryDate = new Date(
                now.getTime() + accessTokenExpiration
        );


        JwtBuilder builder = Jwts.builder()
                .subject(dto.getUsername())
                .claim("userId", dto.getUserId())
                .claim("userRole", dto.getUserRole())
                .issuedAt(now)
                .expiration(expiryDate);
// Conditionally add optional claims only if they are not null
        if (dto.getInstituteId() != null) {
            builder.claim("instituteId", dto.getInstituteId());
        }
        if (dto.getInstituteRole() != null) {
            builder.claim("instituteRole", dto.getInstituteRole());
        }
        return builder
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String username) {

        Date now = new Date();

        Date expiryDate = new Date(
                now.getTime() + refreshTokenExpiration
        );

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);

    }

    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());

    }

    public boolean validateToken(String token) {

        try {

            return !isTokenExpired(token);

        } catch (JwtException | IllegalArgumentException ex) {

            return false;

        }

    }

    public GenerateJwtDto extractClaims(String token) {

        Claims claims = extractAllClaims(token);

        GenerateJwtDto dto = new GenerateJwtDto();

        dto.setUsername(claims.getSubject());
        dto.setUserId(claims.get("userId", String.class));
        dto.setUserRole(claims.get("userRole", String.class));
        dto.setInstituteId(claims.get("instituteId", String.class));
        dto.setInstituteRole(claims.get("instituteRole", String.class));

        return dto;

    }


}