package fit.iuh.controllers;

import fit.iuh.dtos.JwtResponse;
import fit.iuh.dtos.LoginRequest;
import fit.iuh.dtos.RegisterUserRequest;
import fit.iuh.repositories.AccountRepository;
import fit.iuh.repositories.CustomerRepository;
import fit.iuh.services.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
   private final AuthenticationManager authenticationManager;
   private final JwtService jwtService;

   @PostMapping()
   public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
      return null;
   }

   @PostMapping("/login")
   public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      loginRequest.getUsername(),
                      loginRequest.getPassword()
              )
      );
      var token = jwtService.generateToken(loginRequest.getUsername());

      return ResponseEntity.ok(new JwtResponse(token));
   }

   @ExceptionHandler(BadCredentialsException.class)
   public ResponseEntity<Void> handleBadCredentials() {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   }
}
