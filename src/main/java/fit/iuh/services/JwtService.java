package fit.iuh.services;

import fit.iuh.config.JwtConfig;
import fit.iuh.models.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {

   private final JwtConfig jwtConfig;

   public String generateToken(Account account) {
      return getString(account, jwtConfig.getAccessTokenExpiration());
   }
   public String generateRefreshToken(Account account) {
      return getString(account, jwtConfig.getRefreshTokenExpiration());
   }

   private String getString(Account account, long tokenExpiration) {
      return Jwts.builder()
              .subject(account.getUsername())
              .claim("email", account.getEmail())
              .issuedAt(new Date())
              .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
              .signWith(jwtConfig.getSecretKey())
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
              .verifyWith(jwtConfig.getSecretKey())
              .build()
              .parseSignedClaims(token)
              .getPayload();
   }

   public String getUsernameFromToken(String token) {
      return getClaims(token).getSubject();
   }
}
