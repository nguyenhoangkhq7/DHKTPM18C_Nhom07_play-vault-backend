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

}