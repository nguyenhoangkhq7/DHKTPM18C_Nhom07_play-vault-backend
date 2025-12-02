package fit.iuh.controllers;

import fit.iuh.dtos.GameRevenueDto;
import fit.iuh.dtos.ReportSummaryDto;
import fit.iuh.dtos.RevenueTrendDto;
import fit.iuh.services.ExportService;
import fit.iuh.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
// @CrossOrigin(origins = "http://localhost:3000") // Bật nếu cần test localhost React
public class ReportController {
    private final ExportService exportService; // Nhớ inject vào constructor
    private final ReportService reportService;

    @GetMapping("/game-revenue")
    public List<GameRevenueDto> getGameRevenue(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return reportService.getGameRevenue(from, to);
    }

    @GetMapping("/summary")
    public ReportSummaryDto getReportSummary(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return reportService.getReportSummary(from, to);
    }

    @GetMapping("/revenue-over-time")
    public List<RevenueTrendDto> getRevenueTrend(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return reportService.getRevenueTrend(from, to);
    }



    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportReport(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(value = "format", defaultValue = "excel") String format,
            @RequestParam(value = "compare", defaultValue = "false") boolean compare
    ) throws IOException {

        ByteArrayInputStream in = exportService.exportReport(from, to, format, compare);

        String filename = "Baocao_" + from + "_to_" + to + "." + (format.equals("csv") ? "csv" : "xlsx");
        MediaType mediaType = format.equals("csv") ? MediaType.TEXT_PLAIN : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(new InputStreamResource(in));
    }
}