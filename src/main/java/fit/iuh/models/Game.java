package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "games")
//@Where(clause = "is_deleted = false")
public class Game {
   @Id
   @Column(name = "game_basic_info_id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @MapsId
   @OneToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "game_basic_info_id", nullable = false)
   private GameBasicInfo gameBasicInfos;

   @Column(name = "release_date")
   private LocalDate releaseDate;

//   @Column(name = "is_deleted")
//   private boolean isDeleted = false;

   @OneToMany(
           mappedBy = "game",
           cascade = CascadeType.ALL,
           fetch = FetchType.LAZY,
           orphanRemoval = true
   )
   private List<Review> reviews;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "promotion_id") // Thêm cột khóa ngoại promotion_id vào bảng games
   private Promotion promotion;


   public BigDecimal getBasicPrice(){
      return this.gameBasicInfos.getPrice();
   }

   public Double getAvgRating(){
      return this.reviews.stream()
              .collect(Collectors.averagingDouble(Review::getRating));
   }

   public void addReview(Customer customer, Integer rating, String comment){
      Review review = new Review();
      review.setCustomer(customer);
      review.setGame(this);
      review.setComment(comment);
      review.setCreatedAt(LocalDate.now());
      review.setRating(rating);
      this.reviews.add(review);
   }
}