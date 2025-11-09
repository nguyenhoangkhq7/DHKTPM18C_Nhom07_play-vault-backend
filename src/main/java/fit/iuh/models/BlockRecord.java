package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "block_records")
public class BlockRecord {
   @Id
   @Column(name = "id", nullable = false)
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

}