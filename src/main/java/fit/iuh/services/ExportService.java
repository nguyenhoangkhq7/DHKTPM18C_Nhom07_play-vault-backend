package fit.iuh.services;

import fit.iuh.dtos.GameRevenueDto;
import fit.iuh.models.enums.OrderStatus;
import fit.iuh.repositories.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final ReportRepository reportRepository;

    public ByteArrayInputStream exportReport(LocalDate from, LocalDate to, String format, boolean compareWithPrevious) throws IOException {
        // 1. Lấy dữ liệu kỳ hiện tại
        List<GameRevenueDto> currentData = reportRepository.getGameRevenueReport(from, to, OrderStatus.COMPLETED);

        // 2. Lấy dữ liệu kỳ trước (nếu được yêu cầu)
        Map<Long, BigDecimal> prevDataMap = null;
        if (compareWithPrevious) {
            long daysDiff = ChronoUnit.DAYS.between(from, to) + 1;
            LocalDate prevFrom = from.minusDays(daysDiff);
            LocalDate prevTo = from.minusDays(1);
            List<GameRevenueDto> prevData = reportRepository.getGameRevenueReport(prevFrom, prevTo, OrderStatus.COMPLETED);
            // Chuyển List thành Map<GameId, Revenue> để tra cứu cho nhanh
            prevDataMap = prevData.stream()
                    .collect(Collectors.toMap(GameRevenueDto::getGameId, GameRevenueDto::getRevenue));
        }

        // 3. Chọn định dạng xuất
        if ("csv".equalsIgnoreCase(format)) {
            return generateCsv(currentData, prevDataMap);
        } else {
            return generateExcel(currentData, prevDataMap);
        }
    }

    // --- LOGIC TẠO EXCEL ---
    private ByteArrayInputStream generateExcel(List<GameRevenueDto> data, Map<Long, BigDecimal> prevDataMap) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Doanh thu Game");

            // Style Header
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.VIOLET.getIndex()); // Màu tím hợp theme
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Tạo Header Row
            Row headerRow = sheet.createRow(0);
            String[] columns = prevDataMap != null 
                ? new String[]{"ID", "Tên Game", "Thể loại", "Lượt bán", "Doanh thu", "Kỳ trước", "Tăng trưởng"}
                : new String[]{"ID", "Tên Game", "Thể loại", "Lượt bán", "Doanh thu"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Đổ dữ liệu
            int rowIdx = 1;
            for (GameRevenueDto game : data) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(game.getGameId());
                row.createCell(1).setCellValue(game.getName());
                row.createCell(2).setCellValue(game.getCategory());
                row.createCell(3).setCellValue(game.getSales());
                row.createCell(4).setCellValue(game.getRevenue().doubleValue());

                if (prevDataMap != null) {
                    BigDecimal prevRev = prevDataMap.getOrDefault(game.getGameId(), BigDecimal.ZERO);
                    row.createCell(5).setCellValue(prevRev.doubleValue());
                    
                    // Tính tăng trưởng
                    String growth = calculateGrowth(game.getRevenue(), prevRev);
                    row.createCell(6).setCellValue(growth);
                }
            }

            // Auto size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // --- LOGIC TẠO CSV ---
    private ByteArrayInputStream generateCsv(List<GameRevenueDto> data, Map<Long, BigDecimal> prevDataMap) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVFormat format = CSVFormat.DEFAULT.builder().setHeader().build();

        try (CSVPrinter printer = new CSVPrinter(new PrintWriter(out), format)) {
            // Header
            List<String> headers = prevDataMap != null
                ? Arrays.asList("ID", "Tên Game", "Thể loại", "Lượt bán", "Doanh thu", "Kỳ trước", "Tăng trưởng")
                : Arrays.asList("ID", "Tên Game", "Thể loại", "Lượt bán", "Doanh thu");
            printer.printRecord(headers);

            // Data
            for (GameRevenueDto game : data) {
                if (prevDataMap != null) {
                    BigDecimal prevRev = prevDataMap.getOrDefault(game.getGameId(), BigDecimal.ZERO);
                    printer.printRecord(
                        game.getGameId(), game.getName(), game.getCategory(), game.getSales(), 
                        game.getRevenue(), prevRev, calculateGrowth(game.getRevenue(), prevRev)
                    );
                } else {
                    printer.printRecord(
                        game.getGameId(), game.getName(), game.getCategory(), game.getSales(), game.getRevenue()
                    );
                }
            }
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private String calculateGrowth(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) return current.compareTo(BigDecimal.ZERO) > 0 ? "+100%" : "0%";
        BigDecimal diff = current.subtract(previous);
        BigDecimal growth = diff.divide(previous, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return String.format("%.1f%%", growth);
    }
}