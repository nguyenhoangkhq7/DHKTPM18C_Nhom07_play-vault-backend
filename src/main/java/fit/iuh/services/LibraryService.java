package fit.iuh.services;

import fit.iuh.dtos.GameFilterDto; // <-- Import DTO
import fit.iuh.models.Customer;
import fit.iuh.models.Game;
import fit.iuh.repositories.CustomerRepository;
// ĐÃ LOẠI BỎ GameSubmissionRepository
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


    // ĐÃ LOẠI BỎ GameSubmissionRepository

    public List<Game> getPurchasedGames(
            String username,
            GameFilterDto filterDto // <-- Nhận DTO
    ) {

        // 1. Lấy các giá trị lọc từ DTO
        String gameName = filterDto.getName();
        BigDecimal minPrice = filterDto.getMinPrice();
        BigDecimal maxPrice = filterDto.getMaxPrice();
        String categoryName = filterDto.getCategoryName();

        // 2. Kiểm tra logic giá
        if (!filterDto.isPriceRangeValid()) {
            return List.of(); // Trả về rỗng nếu min > max
        }

        // 3. Lấy customer + thư viện game
        Customer customer = customerRepository
                .findByAccount_UsernameWithLibrary(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy customer với username: " + username
                ));

        List<Game> allOwnedGames = customer.getLibrary();
        if (allOwnedGames.isEmpty()) return List.of();

        // 4. Bắt đầu lọc (Stream)
        Stream<Game> gameStream = allOwnedGames.stream();

        // 5. Lọc theo Tên Game
        if (gameName != null && !gameName.isBlank()) {
            gameStream = gameStream.filter(game ->
                    game.getGameBasicInfos() != null &&
                            game.getGameBasicInfos().getName() != null &&
                            game.getGameBasicInfos().getName().toLowerCase()
                                    .contains(gameName.toLowerCase())
            );
        }

        // 6. Lọc theo Tên Loại Game
        if (categoryName != null && !categoryName.isBlank()) {
            String cat = categoryName.trim().toLowerCase();
            gameStream = gameStream.filter(game ->
                            game.getGameBasicInfos() != null &&
                                    game.getGameBasicInfos().getCategory() != null &&
                                    game.getGameBasicInfos().getCategory().getName() != null &&
                                    game.getGameBasicInfos().getCategory().getName().toLowerCase().equals(cat)
                    // Dùng .equals() thay .contains() → chính xác hơn (Action ≠ Action-Adventure)
            );
        }

        // 7. Lọc theo Khoảng Giá
        if (minPrice != null || maxPrice != null) {
            gameStream = gameStream.filter(game -> {
                if (game.getGameBasicInfos() == null) return false;

                BigDecimal price = game.getGameBasicInfos().getPrice();
                if (price == null) return false; // An toàn nếu giá null

                // Kiểm tra giá sàn (nếu có)
                if (minPrice != null && price.compareTo(minPrice) < 0) return false;

                // Kiểm tra giá trần (nếu có)
                if (maxPrice != null && price.compareTo(maxPrice) > 0) return false;

                return true; // Vượt qua tất cả
            });
        }

        // 8. Trả về kết quả
//        return gameStream.collect(Collectors.toList());
        return gameStream
                // --- THÊM DÒNG NÀY ---
                // Đảm bảo không bao giờ trả về game có thông tin cơ bản bị null
                .filter(game -> game.getGameBasicInfos() != null)
                // --- KẾT THÚC THÊM ---
                .collect(Collectors.toList());
    }
}