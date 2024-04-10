package com.example.mosawebapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtGenerator {
    public String generateAccessToken(Authentication auth){
        String user = auth.getName();
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + SecurityConstants.TOKEN_EXPIRATION);

        return Jwts.builder()
            .setSubject(user)
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET_TOKEN)
            .compact();
    }

    public String getUserFromJWT(String token){
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(SecurityConstants.SECRET_TOKEN)
            .build().parseClaimsJws(token)
            .getBody();

        return claims.getSubject();
    }

    public boolean isTokenValid(String token){
        try {
            Jwts.parserBuilder().setSigningKey(SecurityConstants.SECRET_TOKEN).build().parseClaimsJws(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public String generateRefreshToken(Map<String, Object> claims, String user){
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(user)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_REFRESH_EXPIRATION))
            .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET_TOKEN)
            .compact();
    }
}
