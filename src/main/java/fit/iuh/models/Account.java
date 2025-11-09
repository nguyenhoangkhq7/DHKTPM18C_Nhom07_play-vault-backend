package fit.iuh.models;

import fit.iuh.models.enums.AccountStatus;
import fit.iuh.models.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "accounts")
public class Account {
   @Id
   @Column(name = "username", nullable = false)
   private String username;

   @Column(name = "password", nullable = false)
   private String password;

   @Column(name = "created_at")
   private LocalDate createdAt;

   @Enumerated(EnumType.STRING)
   @Column(name = "status", nullable = false, length = 50)
   private AccountStatus status;

   @Enumerated(EnumType.STRING)
   @Column(name = "role", nullable = false, length = 50)
   private Role role;

   @Column(name = "email", nullable = false)
   private String email;

   @Column(name = "phone", length = 20)
   private String phone;

   /**
    * So sánh một mật khẩu thô (rawPassword) với mật khẩu đã HASHED
    * lưu trong DB.
    * * @param rawPassword Mật khẩu thô (chưa hash)
    * @param encoder     Bộ mã hóa (ví dụ: BCryptPasswordEncoder)
    * @return true nếu mật khẩu khớp
    */
   public boolean checkPassword(String rawPassword, PasswordEncoder encoder) {
      // Luôn dùng encoder.matches() để so sánh
      return encoder.matches(rawPassword, this.password);
   }

   /**
    * Cập nhật mật khẩu mới (phải được HASHED trước khi gọi).
    * * @param hashedPassword Mật khẩu mới ĐÃ ĐƯỢC HASHED
    */
   public void changePassword(String hashedPassword) {
      this.password = hashedPassword;
   }

   /**
    * Cập nhật email.
    * (Sơ đồ của bạn ghi là Email email, nhưng thuộc tính là String,
    * nên tôi dùng String)
    */
   public void updateEmail(String newEmail) {
      // TODO: Bạn có thể thêm logic kiểm tra định dạng email ở đây
      this.email = newEmail;
   }

   /**
    * Cập nhật số điện thoại.
    */
   public void updatePhone(String newPhone) {
      this.phone = newPhone;
   }

   /**
    * Kiểm tra có phải là Admin.
    */
   public boolean isAdmin() {
      return this.role == Role.ADMIN;
   }

   /**
    * Kiểm tra có phải là Customer.
    */
   public boolean isCustomer() {
      return this.role == Role.CUSTOMER;
   }

   /**
    * Kiểm tra có phải là Publisher.
    */
   public boolean isPublisher() {
      return this.role == Role.PUBLISHER;
   }

}