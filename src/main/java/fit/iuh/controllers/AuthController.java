package fit.iuh.controllers;

import fit.iuh.dtos.*;
import fit.iuh.models.Account;
import fit.iuh.models.Customer;
import fit.iuh.models.PaymentInfo;
import fit.iuh.models.Publisher;
import fit.iuh.models.enums.AccountStatus;
import fit.iuh.repositories.AccountRepository;
import fit.iuh.repositories.CustomerRepository;
import fit.iuh.repositories.PublisherRepository;
import fit.iuh.services.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
   private final AuthenticationManager authenticationManager;
   private final JwtService jwtService;
   private final AccountRepository accountRepository;
   private final PasswordEncoder passwordEncoder;
   private final CustomerRepository customerRepository;
   private final PublisherRepository publisherRepository;

   @PostMapping("/validate")
   public boolean validate(@RequestHeader("Authorization") String authHeader) {
      var token =  authHeader.replace("Bearer ", "");

      return jwtService.validateToken(token);
   }

   @PostMapping("/login")
   public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      loginRequest.getUsername(),
                      loginRequest.getPassword()
              )
      );
      var account = accountRepository.findByUsername(loginRequest.getUsername()).orElseThrow();

      var token = jwtService.generateToken(account);

      return ResponseEntity.ok(new JwtResponse(token));
   }

   @ExceptionHandler(BadCredentialsException.class)
   public ResponseEntity<Void> handleBadCredentials() {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   }

   @PostMapping("/register/customer")
   public ResponseEntity<String> registerCustomer(@Valid @RequestBody CustomerRegisterRequest request) {

      if (accountRepository.existsByUsername(request.getUsername())) {
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username đã tồn tại");
      }

      // tạo account cơ bản
      Account account = new Account();
      account.setUsername(request.getUsername());
      account.setPassword(passwordEncoder.encode(request.getPassword()));
      account.setEmail(request.getEmail());
      account.setPhone(request.getPhone());
      account.setRole(request.getRole());
      account.setStatus(AccountStatus.ACTIVE);
      account.setCreatedAt(LocalDate.now());

      accountRepository.save(account);

      // tạo customer
      Customer customer = new Customer();
      customer.setFullName(request.getFullName());
      customer.setDateOfBirth(request.getDateOfBirth());
      customer.setAccount(account);

      customerRepository.save(customer);

      return ResponseEntity.status(HttpStatus.CREATED).body("Customer đăng ký thành công");
   }

   @PostMapping("/register/publisher")
   public ResponseEntity<String> registerPublisher(@Valid @RequestBody PublisherRegisterRequest request) {

      if (accountRepository.existsByUsername(request.getUsername())) {
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username đã tồn tại");
      }

      Account account = new Account();
      account.setUsername(request.getUsername());
      account.setPassword(passwordEncoder.encode(request.getPassword()));
      account.setEmail(request.getEmail());
      account.setPhone(request.getPhone());
      account.setRole(request.getRole());
      account.setStatus(AccountStatus.ACTIVE);
      account.setCreatedAt(LocalDate.now());

      accountRepository.save(account);

      Publisher publisher = new Publisher();
      publisher.setStudioName(request.getStudioName());
      publisher.setDescription(request.getDescription());
      publisher.setWebsite(request.getWebsite());

      if (publisher.getPaymentInfo() == null) {
         publisher.setPaymentInfo(new PaymentInfo());
      }
      publisher.getPaymentInfo().setPaymentMethod(request.getPaymentMethod());
      publisher.getPaymentInfo().setAccountName(request.getAccountName());
      publisher.getPaymentInfo().setAccountNumber(request.getAccountNumber());
      publisher.getPaymentInfo().setBankName(request.getBankName());

      publisher.setAccount(account);
      publisherRepository.save(publisher);

      return ResponseEntity.status(HttpStatus.CREATED).body("Publisher đăng ký thành công");
   }

   // ---- REGISTER ADMIN ----
   @PostMapping("/register/admin")
   public ResponseEntity<String> registerAdmin(@Valid @RequestBody AccountRegisterRequest request) {

      if (accountRepository.existsByUsername(request.getUsername())) {
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username đã tồn tại");
      }

      Account account = new Account();
      account.setUsername(request.getUsername());
      account.setPassword(passwordEncoder.encode(request.getPassword()));
      account.setEmail(request.getEmail());
      account.setPhone(request.getPhone());
      account.setRole(request.getRole());
      account.setStatus(AccountStatus.ACTIVE);
      account.setCreatedAt(LocalDate.now());

      accountRepository.save(account);

      return ResponseEntity.status(HttpStatus.CREATED).body("Admin đăng ký thành công");
   }
}
