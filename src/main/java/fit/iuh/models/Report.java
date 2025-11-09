package fit.iuh.models;

import fit.iuh.models.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "reports")
public class Report {
   @Id
   @Column(name = "id", nullable = false)
   private Long id;

   @Column(name = "title")
   private String title;

   @Column(name = "description", columnDefinition = "LONGTEXT")
   private String description;

   @Column(name = "handler_note", columnDefinition = "LONGTEXT")
   private String handlerNote;

   @Column(name = "created_at")
   private LocalDate createdAt;

   @Column(name = "resolved_at")
   private LocalDate resolvedAt;

   @Enumerated(EnumType.STRING)
   @Column(name = "status", nullable = false, length = 50)
   private ReportStatus status;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "order_id", nullable = false)
   private Order order;

   @ManyToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "customer_id", nullable = false)
   private Customer customer;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "handler_username")
   private Account handlerUsername;

}