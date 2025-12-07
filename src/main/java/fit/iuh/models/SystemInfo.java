// fit/iuh/models/SystemInfo.java
package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_infos")
@Getter
@Setter
public class SystemInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "os", columnDefinition = "TEXT")
    private String os;

    @Column(name = "cpu", columnDefinition = "TEXT")
    private String cpu;

    @Column(name = "gpu", columnDefinition = "TEXT")
    private String gpu;

    @Column(name = "ram", columnDefinition = "TEXT")
    private String ram;

    @Column(name = "directx_version", length = 100)
    private String directxVersion;

    @Lob
    @Column(name = "dxdiag_content", columnDefinition = "LONGTEXT")
    private String dxdiagContent;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.lastUpdated = LocalDateTime.now();
    }
}