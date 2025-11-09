package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category {
   @Id
   @Column(name = "id", nullable = false)
   private Long id;

   @Column(name = "name", nullable = false)
   private String name;

   @Column(name = "description", columnDefinition = "LONGTEXT")
   private String description;

}