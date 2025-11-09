package fit.iuh.filters;

import fit.iuh.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
   private final JwtService jwtService;

   @Override
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
      var authHeader = request.getHeader("Authorization");

      // public endpoint
      if(authHeader == null || !authHeader.startsWith("Bearer ")) {
         filterChain.doFilter(request, response);
         return;
      }

      // invalid
      var token = authHeader.replace("Bearer ", "");
      if(!jwtService.validateToken(token)) {
         filterChain.doFilter(request, response);
         return;
      }

      // valid
      var authentication = new UsernamePasswordAuthenticationToken(
              jwtService.getUsernameFromToken(token),
              null,
              null
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);

      filterChain.doFilter(request, response);
   }
}
