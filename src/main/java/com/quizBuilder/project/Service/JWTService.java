package com.quizBuilder.project.Service;

import com.quizBuilder.project.Entity.JWTToken;
import com.quizBuilder.project.Entity.User;
import com.quizBuilder.project.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@Service
public class JWTService {
    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secretKey}")
    private String JWTSecretKey;

    @Value("${jwt.expiry}")
    private long expirationTime;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(JWTSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(User user) {

        Date now = new Date();
        Date expiryTime = new Date(now.getTime() + expirationTime);

        String token =  Jwts.builder()
                .setSubject(user.getName())
                .claim("userId", user.getId())
                .setIssuedAt(now)
                .setExpiration(expiryTime)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return token;
    }


    public Boolean validateToken(String jwtToken){
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();


            return claims.getExpiration() == null
                    || claims.getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long extractUserIdFromToken(String jwtToken){

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(
                        Keys.hmacShaKeyFor(JWTSecretKey.getBytes(StandardCharsets.UTF_8))
                )
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

        Long user_id = claims.get("userId", Long.class);

        return user_id;
    }
}
