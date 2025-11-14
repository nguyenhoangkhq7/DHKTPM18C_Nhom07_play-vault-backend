package fit.iuh.models;

import fit.iuh.models.enums.Os;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "system_requirements")
public class SystemRequirement {
   @Id
   @Column(name = "id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Enumerated(EnumType.STRING)
   @Column(name = "os", length = 50)
   private Os os;

   @Column(name = "cpu")
   private String cpu;

   @Column(name = "gpu")
   private String gpu;

   @Column(name = "storage", length = 100)
   private String storage;

   @Column(name = "ram", length = 100)
   private String ram;

}