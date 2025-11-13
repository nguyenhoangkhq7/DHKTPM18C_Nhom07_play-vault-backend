package fit.iuh.services;

import fit.iuh.models.Customer;
import fit.iuh.models.Game;
import fit.iuh.models.GameSubmission; // Import mới
import fit.iuh.models.enums.SubmissionStatus; // Import mới
import fit.iuh.repositories.CustomerRepository;
import fit.iuh.repositories.GameSubmissionRepository; // Import mới
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LibraryService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GameSubmissionRepository gameSubmissionRepository; // <-- TIÊM REPO MỚI

    public List<Game> getPurchasedGames(
            String username,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String status // <-- THÊM THAM SỐ MỚI
    ) {

        // 1. Lấy Customer và TOÀN BỘ thư viện của họ (giữ nguyên)
        Customer customer = customerRepository
                .findByAccount_UsernameWithLibrary(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy customer với username: " + username
                ));

        // 2. Lấy danh sách tất cả game đã mua (giữ nguyên)
        List<Game> allOwnedGames = customer.getLibrary();

        // 3. Lọc danh sách bằng Java Stream
        Stream<Game> gameStream = allOwnedGames.stream();

        // Lọc theo Category (giữ nguyên)
        if (categoryId != null) {
            gameStream = gameStream.filter(game ->
                    game.getGameBasicInfos() != null &&
                            game.getGameBasicInfos().getCategory() != null &&
                            game.getGameBasicInfos().getCategory().getId().equals(categoryId)
            );
        }

        // Lọc theo Giá Tối Thiểu (giữ nguyên)
        if (minPrice != null) {
            gameStream = gameStream.filter(game ->
                    game.getGameBasicInfos() != null &&
                            game.getGameBasicInfos().getPrice().compareTo(minPrice) >= 0
            );
        }

        // Lọc theo Giá Tối Đa (giữ nguyên)
        if (maxPrice != null) {
            gameStream = gameStream.filter(game ->
                    game.getGameBasicInfos() != null &&
                            game.getGameBasicInfos().getPrice().compareTo(maxPrice) <= 0
            );
        }

        // --- 4. LỌC THEO TRẠNG THÁI (MỚI) ---
        if (status != null && !status.isEmpty()) {
            SubmissionStatus filterStatus;
            try {
                // Chuyển string (VD: "APPROVED") thành Enum
                filterStatus = SubmissionStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Nếu frontend gửi "abc", trả về danh sách rỗng
                return List.of();
            }

            final SubmissionStatus finalStatus = filterStatus; // Cần cho lambda

            gameStream = gameStream.filter(game -> {
                if (game.getGameBasicInfos() == null) return false;

                // !!! CẢNH BÁO: N+1 QUERY !!!
                // Gọi DB cho MỖI game để tìm trạng thái
                GameSubmission submission = gameSubmissionRepository
                        .findById(game.getGameBasicInfos().getId())
                        .orElse(null);

                if (submission == null) return false;
                return submission.getStatus() == finalStatus;
            });
        }
        // --- HẾT PHẦN LỌC TRẠNG THÁI ---

        // 5. Trả về danh sách đã lọc (giữ nguyên)
        return gameStream.collect(Collectors.toList());
    }
}