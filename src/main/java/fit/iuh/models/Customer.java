package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {
   @Id
   @Column(name = "id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "full_name")
   private String fullName;

   @Column(name = "date_of_birth")
   private LocalDate dateOfBirth;

   @ColumnDefault("0.00")
   @Column(name = "balance", precision = 10, scale = 2)
   private BigDecimal balance;

    @Column(name = "avatar_url")
    private String avatarUrl;

   @OneToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "account_username", nullable = false)
   private Account account;

   @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "cart_id")
   private Cart cart;


    @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
           name = "user_libraries",
           joinColumns = @JoinColumn(name = "customer_id"),
           inverseJoinColumns = @JoinColumn(name = "game_id")
   )
   private List<Game> library;

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
           name = "wishlists",
           joinColumns = @JoinColumn(name = "customer_id"),
           inverseJoinColumns = @JoinColumn(name = "game_id")
   )
   private List<Game> wishlist;

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
           name = "user_libraries",
           joinColumns = @JoinColumn(name = "customer_id"),
           inverseJoinColumns = @JoinColumn(name = "game_id")
   )
   private Set<Game> ownedGames = new HashSet<>(); // Tên biến trong Java (CamelCase)

   // Helper method (Giữ nguyên)
   public boolean hasOwnedGame(Long gameId) {
      return ownedGames.stream().anyMatch(g -> g.getId().equals(gameId));
   }
}