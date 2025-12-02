package fit.iuh.services;

import fit.iuh.dtos.GameRevenueDto;
import fit.iuh.dtos.ReportSummaryDto;
import fit.iuh.dtos.RevenueTrendDto;
import fit.iuh.models.enums.OrderStatus;
import fit.iuh.repositories.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    // API 1: Danh sách doanh thu Game
    public List<GameRevenueDto> getGameRevenue(LocalDate from, LocalDate to) {
        return reportRepository.getGameRevenueReport(from, to, OrderStatus.COMPLETED);
    }

    // API 2: Biểu đồ xu hướng
    public List<RevenueTrendDto> getRevenueTrend(LocalDate from, LocalDate to) {
        return reportRepository.getRevenueTrend(from, to, OrderStatus.COMPLETED);
    }

    // API 3: Tổng quan Dashboard (Có tính % tăng trưởng)
    public ReportSummaryDto getReportSummary(LocalDate from, LocalDate to) {
        // 1. Tính toán khoảng thời gian kỳ trước (Previous Period)
        long daysDiff = ChronoUnit.DAYS.between(from, to) + 1; // +1 để tính cả ngày bắt đầu
        LocalDate prevFrom = from.minusDays(daysDiff);
        LocalDate prevTo = from.minusDays(1);

        // 2. Lấy dữ liệu kỳ hiện tại (Current)
        BigDecimal currentRevenue = reportRepository.sumTotalRevenue(from, to, OrderStatus.COMPLETED);
        Long currentOrders = reportRepository.countTotalOrders(from, to, OrderStatus.COMPLETED);
        Long currentSoldGames = reportRepository.countSoldGames(from, to, OrderStatus.COMPLETED);
        Long currentNewUsers = reportRepository.countNewUsers(from, to);

        // 3. Lấy dữ liệu kỳ trước (Previous)
        BigDecimal prevRevenue = reportRepository.sumTotalRevenue(prevFrom, prevTo, OrderStatus.COMPLETED);
        Long prevOrders = reportRepository.countTotalOrders(prevFrom, prevTo, OrderStatus.COMPLETED);
        Long prevSoldGames = reportRepository.countSoldGames(prevFrom, prevTo, OrderStatus.COMPLETED);
        Long prevNewUsers = reportRepository.countNewUsers(prevFrom, prevTo);

        // 4. Tính % tăng trưởng và trả về DTO
        return ReportSummaryDto.builder()
                .totalRevenue(currentRevenue)
                .revenueGrowth(calculateGrowth(currentRevenue, prevRevenue))
                .totalOrders(currentOrders)
                .orderGrowth(calculateGrowth(currentOrders, prevOrders))
                .soldGames(currentSoldGames)
                .soldGamesGrowth(calculateGrowth(currentSoldGames, prevSoldGames))
                .newUsers(currentNewUsers)
                .userGrowth(calculateGrowth(currentNewUsers, prevNewUsers))
                .build();
    }

    // Hàm helper tính % tăng trưởng
    // Công thức: ((Current - Previous) / Previous) * 100
    private Double calculateGrowth(Number current, Number previous) {
        double curVal = current.doubleValue();
        double prevVal = previous.doubleValue();

        if (prevVal == 0) {
            return curVal > 0 ? 100.0 : 0.0; // Nếu kỳ trước = 0, kỳ này > 0 thì coi là tăng 100%
        }

        double growth = ((curVal - prevVal) / prevVal) * 100;
        
        // Làm tròn 1 chữ số thập phân
        BigDecimal bd = new BigDecimal(Double.toString(growth));
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}