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

   /**
    * Kiểm tra xem giao dịch đã hoàn tất (thành công, thất bại hoặc bị hủy) hay chưa.
    */
   public boolean isFinalized() {
      return this.status == PaymentStatus.SUCCESS ||
              this.status == PaymentStatus.FAILED ||
              this.status == PaymentStatus.PENDING;
   }

   /**
    * Kiểm tra xem số tiền thanh toán có hợp lệ (> 0) hay không.
    */
   public void validateAmount() {
      if (this.amount == null || this.amount.compareTo(BigDecimal.ZERO) <= 0) {
         throw new IllegalArgumentException("Số tiền thanh toán phải lớn hơn 0.");
      }
   }

   /**
    * Liên kết thanh toán này với một hóa đơn.
    */
   public void linkInvoice(Invoice invoice) {
      this.invoice = invoice;
   }
}