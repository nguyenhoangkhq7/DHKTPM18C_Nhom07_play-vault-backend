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
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id", nullable = false)
   private Long id;

   @Column(name = "created_at", nullable = false)
   private LocalDate createdAt;

   @Enumerated(EnumType.STRING)
   @Column(name = "status", nullable = false, length = 50)
   private OrderStatus status;

   @Column(name = "total", precision = 10, scale = 2)
   private BigDecimal total;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "customer_id", nullable = false)
   private Customer customer;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "payment_id")
   private Payment payment;

   @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<OrderItem> orderItems;

   // ------------------------------------
   // Constructors
   // ------------------------------------
   public Order() {
      this.createdAt = LocalDate.now();
      this.status = OrderStatus.PENDING; // trạng thái mặc định
      this.total = BigDecimal.ZERO;
   }

   // ------------------------------------
   // Business Methods
   // ------------------------------------

   /**
    * Tính tổng tiền đơn hàng từ danh sách OrderItem
    */
   public BigDecimal totalPrice() {
      if (orderItems == null || orderItems.isEmpty()) {
         return BigDecimal.ZERO;
      }
      this.total = orderItems.stream()
              .map(OrderItem::getSubtotal)
              .reduce(BigDecimal.ZERO, BigDecimal::add);
      return this.total;
   }

   /**
    * Hoàn tất đơn hàng
    */
   public void completeOrder() {
      if (this.status == OrderStatus.CANCELLED) {
         throw new IllegalStateException("Không thể hoàn tất đơn hàng đã bị hủy.");
      }
      this.status = OrderStatus.COMPLETED;
   }

   /**
    * Hủy đơn hàng
    */
   public void cancelOrder() {
      if (this.status == OrderStatus.COMPLETED) {
         throw new IllegalStateException("Không thể hủy đơn hàng đã hoàn tất.");
      }
      this.status = OrderStatus.CANCELLED;
   }

   /**
    * Sinh hóa đơn (ví dụ minh họa)
    */
   public String generateInvoice() {
      return String.format("""
                ----- INVOICE -----
                Order ID   : %d
                Customer   : %s
                Created At : %s
                Status     : %s
                Total      : %s
                -------------------
                """,
              id,
              customer != null ? customer.getFullName() : "Unknown",
              createdAt,
              status,
              total != null ? total : "0.00"
      );
   }
}
