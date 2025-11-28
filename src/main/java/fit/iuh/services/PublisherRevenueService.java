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
        LocalDate from = fromDate != null ? fromDate : LocalDate.of(2000, 1, 1);
        LocalDate to = toDate != null ? toDate : LocalDate.now();

        // Tổng doanh thu hiện tại
        BigDecimal revenue = orderItemRepo.getRevenueByPublisher(publisherId, from, to);
        if (revenue == null) revenue = BigDecimal.ZERO;

        // Doanh thu kỳ trước (cùng kỳ năm ngoái)
        BigDecimal prevRevenue = orderItemRepo.getRevenueByPublisher(publisherId,
                from.minusYears(1), to.minusYears(1));
        if (prevRevenue == null) prevRevenue = BigDecimal.ZERO;

        // Tính % tăng trưởng
        BigDecimal growth = BigDecimal.ZERO;
        if (prevRevenue.compareTo(BigDecimal.ZERO) > 0) {
            growth = revenue.subtract(prevRevenue)
                    .divide(prevRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        // Lấy danh sách OrderItem để đếm game + người chơi
        List<OrderItem> revenueItems = orderItemRepo.findRevenueByGame(publisherId, from, to);
        List<OrderItemDto> items = orderItemMapper.toListDto(revenueItems);

        long totalGames = items.stream()
                .map(OrderItemDto::getGameId)
                .distinct()
                .count();

        long totalPlayers = items.stream()
                .map(item -> item.getOrderId())
                .distinct()
                .count();

        BigDecimal avgPerGame = totalGames > 0
                ? revenue.divide(BigDecimal.valueOf(totalGames), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new RevenueSummaryDto(revenue, growth, totalPlayers, totalGames, avgPerGame);
    }

    // 2. Doanh thu theo từng game
    public List<OrderItemDto> getRevenueByGame(Long publisherId, LocalDate fromDate, LocalDate toDate) {
        LocalDate from = fromDate != null ? fromDate : LocalDate.of(2000, 1, 1);
        LocalDate to = toDate != null ? toDate : LocalDate.now();

        List<OrderItem> items = orderItemRepo.findRevenueByGame(publisherId, from, to);
        return orderItemMapper.toListDto(items);
    }

    // 3. Doanh thu theo tháng
    public List<MonthlyRevenueDto> getMonthlyRevenue(Long publisherId, int year) {
        List<Object[]> rawData = orderItemRepo.getMonthlyRevenueRaw(publisherId, year);
        return rawData.stream()
                .map(row -> new MonthlyRevenueDto((Integer) row[0], (BigDecimal) row[1]))
                .collect(Collectors.toList());
    }
}