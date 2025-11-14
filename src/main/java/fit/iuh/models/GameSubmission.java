package fit.iuh.models;

import fit.iuh.models.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;

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

    // ----------------------------
    // Business methods
    // ----------------------------

    /**
     * Approve submission: set status thành APPROVED, lưu reviewer và thời gian.
     * Không tự persist; service/caller sẽ save thay đổi này.
     *
     * @param admin account người approve (không null)
     * @throws IllegalArgumentException nếu admin == null
     * @throws IllegalStateException nếu không hợp lệ theo business rule (tùy bạn bật)
     */
    public void approve(Account admin) {
        if (admin == null) {
            throw new IllegalArgumentException("admin không được null");
        }

        // Nếu bạn muốn ép chỉ approve khi đang ở trạng thái SUBMITTED, mở comment bên dưới:
        // if (this.status != SubmissionStatus.SUBMITTED) {
        //     throw new IllegalStateException("Chỉ có thể approve khi đang ở trạng thái SUBMITTED");
        // }

        this.status = SubmissionStatus.APPROVED;
        this.reviewerUsername = admin;
        this.reviewedAt = LocalDate.now();
        // Xoá lý do reject nếu có
        this.rejectReason = null;
    }

    /**
     * Reject submission: set status thành REJECTED, lưu reviewer, thời gian và lý do từ chối.
     *
     * @param admin  account người reject (không null)
     * @param reason lý do từ chối (có thể là null/empty)
     * @throws IllegalArgumentException nếu admin == null
     */
    public void reject(Account admin, String reason) {
        if (admin == null) {
            throw new IllegalArgumentException("admin không được null");
        }

        // Nếu muốn chỉ cho reject khi đang SUBMITTED:
        // if (this.status != SubmissionStatus.SUBMITTED) {
        //     throw new IllegalStateException("Chỉ có thể reject khi đang ở trạng thái SUBMITTED");
        // }

        this.status = SubmissionStatus.REJECTED;
        this.reviewerUsername = admin;
        this.reviewedAt = LocalDate.now();
        this.rejectReason = (reason == null) ? "" : reason;
    }

    /**
     * Tạo đối tượng Game từ dữ liệu của submission. Không persist ở đây.
     * Vì model Game dùng @MapsId với GameBasicInfo (chung id), ta set GameBasicInfo hiện có.
     *
     * LƯU Ý:
     * - Nếu bạn muốn Game có releaseDate cụ thể, chỉnh logic (ví dụ set LocalDate.now() hoặc lấy từ gameBasicInfos).
     * - Service gọi phương thức này rồi gọi gameRepository.save(game) trong transaction.
     *
     * @return một instance Game được khởi tạo từ dữ liệu trong submission
     * @throws IllegalStateException nếu gameBasicInfos == null
     */
    public Game toGameEntity() {
        if (this.gameBasicInfos == null) {
            throw new IllegalStateException("Không có gameBasicInfos để tạo Game");
        }

        Game game = new Game();

        // Do Game dùng @MapsId với GameBasicInfo, set tham chiếu:
        game.setGameBasicInfos(this.gameBasicInfos);

        // Nếu bạn muốn set releaseDate cho Game, có thể lấy từ submittedAt hoặc đặt mặc định:
        // Ví dụ: đặt releaseDate là ngày duyệt (reviewedAt) nếu đã duyệt, ngược lại null
        if (this.reviewedAt != null) {
            game.setReleaseDate(this.reviewedAt);
        } else {
            game.setReleaseDate(LocalDate.now()); // hoặc null tuỳ business
        }

        // reviews để trống ban đầu
        game.setReviews(new ArrayList<>());

        return game;
    }

    /**
     * Trả về một bản sao (shallow copy) của GameBasicInfo trong submission.
     * Useful khi bạn muốn tạo một bản mới của GameBasicInfo (ví dụ: sanitized copy trước khi persist)
     * Chú ý: các quan hệ ManyToOne (category, publisher, ...) được copy tham chiếu (không clone sâu).
     *
     * @return một đối tượng GameBasicInfo mới chứa dữ liệu từ this.gameBasicInfos
     * @throws IllegalStateException nếu gameBasicInfos == null
     */
    public GameBasicInfo toGameBasicInfoCopy() {
        if (this.gameBasicInfos == null) {
            throw new IllegalStateException("Không có gameBasicInfos để copy");
        }

        GameBasicInfo src = this.gameBasicInfos;
        GameBasicInfo copy = new GameBasicInfo();

        // KHÔNG set id — để JPA sinh id mới khi persist (nếu bạn muốn giữ id cũ thì cần xử lý khác)
        copy.setName(src.getName());
        copy.setShortDescription(src.getShortDescription());
        copy.setDescription(src.getDescription());
        copy.setPrice(src.getPrice());
        copy.setFilePath(src.getFilePath());
        copy.setThumbnail(src.getThumbnail());
        copy.setTrailerUrl(src.getTrailerUrl());
        copy.setRequiredAge(src.getRequiredAge());
        copy.setIsSupportController(src.getIsSupportController());
        // copy quan hệ bằng tham chiếu (nếu muốn lookup mới, service phải xử lý)
        copy.setCategory(src.getCategory());
        copy.setPublisher(src.getPublisher());
        copy.setSystemRequirement(src.getSystemRequirement());
        // copy platforms (shallow copy list)
        if (src.getPlatforms() != null) {
            copy.setPlatforms(new ArrayList<>(src.getPlatforms()));
        }

        return copy;
    }
}