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

   @Column(name = "price", precision = 10, scale = 2)
   private BigDecimal price;

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

}