package fit.iuh.dtos;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class JwtResponse {
   private String token;
   private UserDto user;

   public JwtResponse(String token) {
      this.token = token;
   }
   public JwtResponse(String token, UserDto user) {
      this.token = token;
      this.user = user;
   }
}
