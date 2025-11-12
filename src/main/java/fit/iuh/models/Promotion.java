package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

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

}