package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "cart_items")
public class CartItem {
   @Id
   @Column(name = "id", nullable = false)
   private Long id;

   @Column(name = "price", precision = 10, scale = 2)
   private BigDecimal price;

   @Column(name = "discount", precision = 10, scale = 2)
   private BigDecimal discount;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "cart_id", nullable = false)
   private Cart cart;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "game_id", nullable = false)
   private Game game;

}