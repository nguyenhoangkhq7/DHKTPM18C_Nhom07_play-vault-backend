package fit.iuh.models;

import fit.iuh.models.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
   @Id
   @Column(name = "id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "created_at")
   private LocalDate createdAt;

   @Column(name = "total", precision = 10, scale = 2)
   private BigDecimal total;

   @Enumerated(EnumType.STRING)
   @Column(name = "status", nullable = false, length = 50)
   private OrderStatus status;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "customer_id", nullable = false)
   private Customer customer;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "payment_id")
   private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;
}