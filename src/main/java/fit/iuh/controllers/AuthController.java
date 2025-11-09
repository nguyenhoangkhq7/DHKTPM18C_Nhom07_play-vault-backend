package fit.iuh.controllers;

import fit.iuh.dtos.LoginRequest;
import fit.iuh.dtos.RegisterUserRequest;
import fit.iuh.repositories.AccountRepository;
import fit.iuh.repositories.CustomerRepository;
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


   @PostMapping()
   public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
      return null;
   }

   @PostMapping("/login")
   public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest loginRequest) {
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      loginRequest.getUsername(),
                      loginRequest.getPassword()
              )
      );
      return ResponseEntity.ok().build();
   }

   @ExceptionHandler(BadCredentialsException.class)
   public ResponseEntity<Void> handleBadCredentials() {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   }
}
