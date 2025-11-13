package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItem {

   @Id
   @Column(name = "id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   // Giá gốc của sản phẩm
   @Column(name = "price", precision = 10, scale = 2, nullable = false)
   private BigDecimal price;

   // Số lượng sản phẩm trong đơn
   @Column(name = "quantity", nullable = false)
   private int quantity;

   // Tổng tiền của dòng này (sau khi áp dụng khuyến mãi nếu có)
   @Column(name = "total", precision = 10, scale = 2)
   private BigDecimal total;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "order_id", nullable = false)
   private Order order;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "game_id", nullable = false)
   private Game game;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "promotion_id")
   private Promotion promotion;

   // --------------------------
   // Business logic
   // --------------------------

   /**
    * Tính thành tiền cho sản phẩm này (đã bao gồm khuyến mãi nếu có)
    */
   public BigDecimal getSubtotal() {
      BigDecimal baseTotal = price.multiply(BigDecimal.valueOf(quantity));

      // Nếu có promotion (giảm giá), giả sử có phương thức getDiscountRate() (VD: 0.1 = 10%)
      if (promotion != null && promotion.getDiscountRate() != null) {
         BigDecimal discount = baseTotal.multiply(promotion.getDiscountRate());
         baseTotal = baseTotal.subtract(discount);
      }

      this.total = baseTotal;
      return baseTotal;
   }
}
