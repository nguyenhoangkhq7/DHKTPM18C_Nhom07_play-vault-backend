package fit.iuh.config;

import fit.iuh.filters.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
   private final JwtAuthenticationFilter jwtAuthenticationFilter;

   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }

   @Bean
   public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
      return authConfig.getAuthenticationManager();
   }
   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      return http
              .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 👈 Bổ sung dòng này
              .csrf(AbstractHttpConfigurer::disable)
              .sessionManagement(c ->
                      c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
              )
              .authorizeHttpRequests(c -> c
                      .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                      .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                      .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                      .requestMatchers(HttpMethod.POST, "/api/auth/register/**").permitAll()
                      .requestMatchers(HttpMethod.POST, "/api/publishers/register").permitAll()
                      .requestMatchers(HttpMethod.GET, "/api/games", "/api/games/**").permitAll()
                      .requestMatchers(HttpMethod.GET, "/api/games/top", "/api/games/top/**").permitAll()
                      .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                      .requestMatchers(HttpMethod.POST, "/api/drive/upload").permitAll()
                      .requestMatchers(HttpMethod.POST, "/api/games").authenticated()
                      .requestMatchers(HttpMethod.GET, "/api/chat/**").permitAll()
                      .requestMatchers("/api/drive/auth", "/api/drive/oauth2callback").permitAll()
                      .requestMatchers("/images/**").permitAll()
                      .requestMatchers("/api/wallet/**").authenticated()
                      .anyRequest().authenticated()
              )
              .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
              .exceptionHandling(c -> {
                 c.accessDeniedHandler((req, rsp, e)
                         -> rsp.sendError(HttpStatus.FORBIDDEN.value()));
                 c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
              })
              .securityContext(context -> context.requireExplicitSave(false))
              .build();
   }
   @Bean
   public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowedOrigins(List.of("http://localhost:5173")); // URL frontend
      configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
      configuration.setAllowedHeaders(List.of("*"));
      configuration.setAllowCredentials(true); // cho phép cookie/token

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
   }
}
