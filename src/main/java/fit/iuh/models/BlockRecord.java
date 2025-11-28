package fit.iuh.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "block_records")
@AllArgsConstructor
@NoArgsConstructor
public class BlockRecord {
   @Id
   @Column(name = "id", nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "is_block")
   private Boolean isBlock;

   @Column(name = "created_at")
   private LocalDate createdAt;

   @Column(name = "reason", columnDefinition = "LONGTEXT")
   private String reason;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "account_username", nullable = false)
   private Account account;

   public BlockRecord(Account account, String reason, boolean isBlock, LocalDate blockedAt) {
      this.account = account;
      this.reason = reason;
      this.isBlock = isBlock;
      this.createdAt = blockedAt;
   }

}