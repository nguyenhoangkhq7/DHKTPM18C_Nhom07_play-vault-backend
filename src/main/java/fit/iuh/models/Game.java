package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "games")
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

   @OneToMany(
           mappedBy = "game",
           cascade = CascadeType.ALL,
           fetch = FetchType.LAZY,
           orphanRemoval = true
   )
   private List<Review> reviews;
}