package fit.iuh.models;

import fit.iuh.models.enums.RequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "publisher_requests")
public class PublisherRequest {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id", nullable = false)
   private Long id;

   @NotNull
   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "account_username", nullable = false)
   private Account accountUsername;

   @NotNull
   @Enumerated(EnumType.STRING)
   @Column(name = "status", nullable = false)
   private RequestStatus status;

   @NotNull
   @Column(name = "created_at", nullable = false)
   private LocalDate createdAt;

   @NotNull
   @Column(name = "updated_at", nullable = false)
   private LocalDate updatedAt;

}