package fit.iuh.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {
   @Id
   @Column(name = "id", nullable = false)
   private Long id;

   @ColumnDefault("0.00")
   @Column(name = "total_price", precision = 10, scale = 2)
   private BigDecimal totalPrice;

}