package com.example.SISproject.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {
    @Value("${jwt.secret.key}")
    public String SECRET_KEY;
    public Date expiration = new Date(System.currentTimeMillis() + Duration.ofMinutes(15).toMillis());

    public String generateToken(String email){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims,email);
    }

    public String getUserEmail(String token){
        return getClaims(token,Claims::getSubject);
    }

    public Date getExpiration(String token){
        return getClaims(token, Claims::getExpiration);
    }

    public <T> T getClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String email = getUserEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private String createToken(Map<String,Object> claims, String email){
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(getSignKey())
                .compact();
    }

    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getAllClaims(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token){
        return getExpiration(token).before(new Date());
    }
}
