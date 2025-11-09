package fit.iuh.services;

import fit.iuh.models.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

   @Value("${spring.jwt.secret}")
   private String secret;

   public String generateToken(Account acocunt) {
      final long tokenExpiration = 86400;

      return Jwts.builder()
              .subject(acocunt.getUsername())
              .claim("email", acocunt.getEmail())
              .issuedAt(new Date())
              .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
              .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
              .compact();
   }

   public boolean validateToken(String token) {
      try {
         var claims = getClaims(token);

         return claims.getExpiration().after(new Date());
      } catch (JwtException e) {
         return false;
      }
   }

   private Claims getClaims(String token) {
      return Jwts.parser()
              .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
              .build()
              .parseSignedClaims(token)
              .getPayload();
   }

   public String getUsernameFromToken(String token) {
      return getClaims(token).getSubject();
   }
}
