package fit.iuh.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
   private String username;
   private String email;
   private String phone;
   private String role;

   // Customer info
   private Long customerId;
   private String fullName;
   private BigDecimal balance;
   private String avatarUrl;

   // Publisher info
   private Long publisherId;
   private String studioName;
   private String description;
   private String website;

   // Constructor, getters, setters
}

