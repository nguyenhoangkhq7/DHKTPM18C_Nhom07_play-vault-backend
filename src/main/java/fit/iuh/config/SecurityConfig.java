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
              .sessionManagement(c ->
                      c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
              )
              .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(c -> c
                      .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                      .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                      .requestMatchers(HttpMethod.POST, "/auth/register/**").permitAll()

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
}
