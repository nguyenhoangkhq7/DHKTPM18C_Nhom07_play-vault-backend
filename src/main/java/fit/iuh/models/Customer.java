package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

   @OneToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "account_username", nullable = false)
   private Account account;

   @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "cart_id")
   private Cart cart;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

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

}