package fit.iuh.services;

import fit.iuh.dtos.*;
import fit.iuh.mappers.OrderItemMapper;
import fit.iuh.models.OrderItem;
import fit.iuh.repositories.OrderItemRepository;
import fit.iuh.repositories.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublisherRevenueService {

    private final OrderItemRepository orderItemRepo;
    private final OrderItemMapper orderItemMapper;
    private final PublisherRepository publisherRepository; // Đã có sẵn

    // Lấy Publisher ID từ username (dùng trong Controller)
    public Long getPublisherIdByUsername(String username) {
        return publisherRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Publisher với username: " + username))
                .getId();
    }
    // 1. Tổng hợp doanh thu (Dashboard chính)
    public RevenueSummaryDto getSummary(Long publisherId, LocalDate fromDate, LocalDate toDate) {
        // QUAN TRỌNG: Vì Order.createdAt là LocalDate (không có giờ)
        // Nên khi truyền to = 2025-11-29 → nó hiểu là đến hết ngày 29/11
        // Nhưng JPA so sánh LocalDate với LocalDate → vẫn đúng!
        // → Không cần cộng 1 ngày ở đây (vì cùng kiểu)

        LocalDate from = fromDate != null ? fromDate : LocalDate.of(2000, 1, 1);
        LocalDate to = toDate != null ? toDate : LocalDate.now();

        // Nếu bạn để nguyên query dùng BETWEEN → VỚI LocalDate LÀ HOÀN TOÀN ĐÚNG!
        // Không bị lỗi như LocalDateTime

        BigDecimal revenue = orderItemRepo.getRevenueByPublisher(publisherId, from, to);
        if (revenue == null) revenue = BigDecimal.ZERO;

        BigDecimal prevRevenue = orderItemRepo.getRevenueByPublisher(publisherId,
                from.minusYears(1), to.minusYears(1));
        if (prevRevenue == null) prevRevenue = BigDecimal.ZERO;

        BigDecimal growth = BigDecimal.ZERO;
        if (prevRevenue.compareTo(BigDecimal.ZERO) > 0) {
            growth = revenue.subtract(prevRevenue)
                    .divide(prevRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        List<GameRevenueDto> revenueItems = orderItemRepo.findRevenueByGame(publisherId, from, to);

        long totalGames = revenueItems.size();
        long totalPlayers = revenueItems.stream()
                .mapToLong(GameRevenueDto::totalOrders)
                .sum();

        BigDecimal avgPerGame = totalGames > 0
                ? revenue.divide(BigDecimal.valueOf(totalGames), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new RevenueSummaryDto(revenue, growth, totalPlayers, totalGames, avgPerGame);
    }

    // 2. Doanh thu theo từng game
    public List<GameRevenueDto> getRevenueByGame(Long publisherId, LocalDate fromDate, LocalDate toDate) {
        LocalDate from = fromDate != null ? fromDate : LocalDate.of(2000, 1, 1);
        LocalDate to = toDate != null ? toDate : LocalDate.now();

        // Vì createdAt là LocalDate → BETWEEN hoạt động chính xác 100%
        return orderItemRepo.findRevenueByGame(publisherId, from, to);
    }

    // 3. Doanh thu theo tháng
    public List<MonthlyRevenueDto> getMonthlyRevenue(Long publisherId, int year) {
        List<Object[]> rawData = orderItemRepo.getMonthlyRevenueRaw(publisherId, year);
        return rawData.stream()
                .map(row -> new MonthlyRevenueDto((Integer) row[0], (BigDecimal) row[1]))
                .collect(Collectors.toList());
    }
    // 4. Doanh thu theo tháng của 1 game cụ thể
    public List<MonthlyRevenueDto> getGameMonthlyRevenue(Long publisherId, Long gameId, int year) {
        List<Object[]> rawData = orderItemRepo.getGameMonthlyRevenueRaw(publisherId, gameId, year);
        return rawData.stream()
                .map(row -> new MonthlyRevenueDto((Integer) row[0], (BigDecimal) row[1]))
                .collect(Collectors.toList());
    }
}