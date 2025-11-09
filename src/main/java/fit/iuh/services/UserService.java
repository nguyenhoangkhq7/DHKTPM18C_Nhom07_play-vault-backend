package fit.iuh.services;

import fit.iuh.models.Account;
import fit.iuh.models.enums.AccountStatus;
import fit.iuh.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
   private AccountRepository accountRepository;

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      Account account = accountRepository.findByUsername(username)
              .orElseThrow(() -> new UsernameNotFoundException("Account not found: " + username));

      return User.withUsername(account.getUsername())
              .password(account.getPassword())
              .authorities(account.getRole().name())
              .accountLocked(account.getStatus() == AccountStatus.LOCKED)
              .disabled(account.getStatus() == AccountStatus.BANNED)
              .build();
   }
}