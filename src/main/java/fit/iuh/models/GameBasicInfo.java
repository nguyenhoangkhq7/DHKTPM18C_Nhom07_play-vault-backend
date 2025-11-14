package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "game_basic_infos")
public class GameBasicInfo {
   @Id
   @Column(name = "id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "name", nullable = false)
   private String name;

   @Column(name = "short_description", columnDefinition = "LONGTEXT")
   private String shortDescription;

   @Column(name = "description", columnDefinition = "LONGTEXT")
   private String description;

   @ColumnDefault("0.00")
   @Column(name = "price", precision = 10, scale = 2)
   private BigDecimal price;

   @Column(name = "file_path")
   private String filePath;

   @Column(name = "thumbnail")
   private String thumbnail;

   @Column(name = "trailer_url")
   private String trailerUrl;

   @Column(name = "required_age")
   private Integer requiredAge;

   @Column(name = "is_support_controller")
   private Boolean isSupportController;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "category_id")
   private Category category;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "publisher_id")
   private Publisher publisher;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "system_requirement_id")
   private SystemRequirement systemRequirement;

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
           name = "game_platforms",
           joinColumns = @JoinColumn(name = "game_basic_info_id"),
           inverseJoinColumns = @JoinColumn(name = "platform_id")
   )
   private List<Platform> platforms;

}