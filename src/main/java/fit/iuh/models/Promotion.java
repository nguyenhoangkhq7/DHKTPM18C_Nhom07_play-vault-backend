package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "promotions")
public class Promotion {

   @Id
   @Column(name = "id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "name", nullable = false)
   private String name;

   @Column(name = "description", columnDefinition = "LONGTEXT")
   private String description;

   @Column(name = "start_date")
   private LocalDate startDate;

   @Column(name = "end_date")
   private LocalDate endDate;

   @ColumnDefault("0")
   @Column(name = "is_active")
   private Boolean isActive;

   @Column(name = "discount_percent", precision = 5, scale = 2)
   private BigDecimal discountPercent;

   @Column(name = "discount_amount", precision = 10, scale = 2)
   private BigDecimal discountAmount;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "publisher_id", nullable = false)
   private Publisher publisher;

   // **THUỘC TÍNH MỚI: Liên kết Một-Nhiều đến Game**
   @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY)
   private List<Game> games;
   // --------------------------
   // Business logic
   // --------------------------

   /**
    * Trả về tỷ lệ giảm giá (0.1 = 10%), nếu không có thì trả về 0.
    */
   public BigDecimal getDiscountRate() {
      if (discountPercent != null) {
         return discountPercent.divide(BigDecimal.valueOf(100));
      }
      return BigDecimal.ZERO;
   }

   /**
    * Tính số tiền được giảm cho một sản phẩm cụ thể.
    * @param basePrice giá gốc của sản phẩm
    * @return số tiền giảm
    */
   public BigDecimal calculateDiscount(BigDecimal basePrice) {
      if (basePrice == null) return BigDecimal.ZERO;

      BigDecimal discount = BigDecimal.ZERO;

      // Ưu tiên giảm theo phần trăm
      if (discountPercent != null && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
         discount = basePrice.multiply(getDiscountRate());
      }
      // Nếu có giảm theo số tiền cố định, cộng thêm
      if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
         discount = discount.add(discountAmount);
      }

      // Không cho giảm quá giá gốc
      if (discount.compareTo(basePrice) > 0) {
         discount = basePrice;
      }

      return discount;
   }
}
