package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "publishers")
public class Publisher {
   @Id
   @Column(name = "id", nullable = false)
   private Long id;

   @OneToOne(fetch = FetchType.LAZY, optional = false)
   @JoinColumn(name = "account_username", nullable = false)
   private Account account;

   @Column(name = "studio_name", nullable = false)
   private String studioName;

   @Column(name = "description", columnDefinition = "LONGTEXT")
   private String description;

   @Column(name = "website")
   private String website;

   @Column(name = "payment")
   private String payment;

   @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "payment_info_id")
   private PaymentInfo paymentInfo;

}