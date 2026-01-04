package com.coffee.app.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Component
public class JWTCore {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    private SecretKeySpec getSigningKey() {
        byte[] decodedKey = java.util.Base64.getEncoder().encode(jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        decodedKey = java.util.Base64.getDecoder().decode(decodedKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA512");
    }


    public String generateToken(Authentication authentication) {
        UserDetailsImpl userDetails =
                (UserDetailsImpl) authentication.getPrincipal();

        String username = userDetails != null ? userDetails.getUsername() : "anonymous";

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            Date expiration = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return username.equals(userDetails.getUsername()) && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
