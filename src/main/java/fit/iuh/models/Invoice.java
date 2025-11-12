package fit.iuh.models;

import fit.iuh.models.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "invoices")
public class Invoice {
   @Id
   @Column(name = "id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "issue_date")
   private LocalDate issueDate;

   @Column(name = "total_amount", precision = 10, scale = 2)
   private BigDecimal totalAmount;

   @Enumerated(EnumType.STRING)
   @Column(name = "status", nullable = false, length = 50)
   private InvoiceStatus status;

   @OneToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "order_id", nullable = false)
   private Order order;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "customer_id", nullable = false)
   private Customer customer;

}