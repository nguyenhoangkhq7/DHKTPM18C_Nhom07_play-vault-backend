package fit.iuh.models;

import fit.iuh.models.enums.PaymentMethod;
import fit.iuh.models.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {
   @Id
   @Column(name = "id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "amount", precision = 10, scale = 2)
   private BigDecimal amount;

   @Column(name = "payment_date")
   private LocalDate paymentDate;

   @Enumerated(EnumType.STRING)
   @Column(name = "payment_method", length = 50)
   private PaymentMethod paymentMethod;

   @Enumerated(EnumType.STRING)
   @Column(name = "status", length = 50)
   private PaymentStatus status;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "invoice_id")
   private Invoice invoice;

}