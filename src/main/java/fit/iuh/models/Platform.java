package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "platforms")
public class Platform {
   @Id
   @Column(name = "id", nullable = false)
   private Long id;

   @Column(name = "name", nullable = false)
   private String name;
}