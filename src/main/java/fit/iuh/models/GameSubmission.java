package fit.iuh.models;

import fit.iuh.models.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "game_submissions")
public class GameSubmission {
   @Id
   @Column(name = "game_basic_info_id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @MapsId
   @OneToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "game_basic_info_id", nullable = false)
   private GameBasicInfo gameBasicInfos;

   @Enumerated(EnumType.STRING)
   @Column(name = "status", nullable = false, length = 50)
   private SubmissionStatus status;

   @Column(name = "reject_reason", columnDefinition = "LONGTEXT")
   private String rejectReason;

   @Column(name = "submitted_at")
   private LocalDate submittedAt;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "reviewer_username")
   private Account reviewerUsername;

   @Column(name = "reviewed_at")
   private LocalDate reviewedAt;

}