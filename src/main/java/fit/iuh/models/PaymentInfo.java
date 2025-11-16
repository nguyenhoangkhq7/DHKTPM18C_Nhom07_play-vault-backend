package fit.iuh.models;

import fit.iuh.models.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "payment_infos")
public class PaymentInfo {
   @Id
   @Column(name = "id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Enumerated(EnumType.STRING)
   @Column(name = "payment_method", nullable = false, length = 50)
   private PaymentMethod paymentMethod;

   @Column(name = "account_name")
   private String accountName;

   @Column(name = "account_number", length = 100)
   private String accountNumber;

   @Column(name = "bank_name")
   private String bankName;

   @ColumnDefault("0")
   @Column(name = "is_verified")
   private Boolean isVerified;

   /**
    * Trả về số tài khoản đã được che một phần (ví dụ: ****1234).
    */
   public String getMaskedAccountNumber() {
      if (this.accountNumber == null || this.accountNumber.length() < 4) {
         return this.accountNumber;
      }
      int length = this.accountNumber.length();
      // Che tất cả trừ 4 ký tự cuối cùng
      return "*".repeat(length - 4) + this.accountNumber.substring(length - 4);
   }

   /**
    * Đánh dấu thông tin tài khoản đã được xác minh.
    */
   public void verifyAccount() {
      this.isVerified = true;
   }

   /**
    * Hủy xác minh tài khoản.
    */
   public void unverifyAccount() {
      this.isVerified = false;
   }

   /**
    * Kiểm tra xem thông tin có sẵn sàng và đã được xác minh cho giao dịch rút tiền hay không.
    */
   public boolean isReadyForPayout() {
      // Phải được xác minh và các trường quan trọng không được rỗng
      return Boolean.TRUE.equals(this.isVerified) &&
              this.paymentMethod != null &&
              this.accountNumber != null &&
              !this.accountNumber.trim().isEmpty();
   }

}