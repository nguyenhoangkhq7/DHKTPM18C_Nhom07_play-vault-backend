package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "reviews")
public class Review {
   @Id
   @Column(name = "id", nullable = false)
   private Long id;

   @Column(name = "rating")
   private Integer rating;

   @Column(name = "comment", columnDefinition = "LONGTEXT")
   private String comment;

   @Column(name = "created_at")
   private LocalDate createdAt;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "customer_id", nullable = false)
   private Customer customer;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "game_id", nullable = false)
   private Game game;
}