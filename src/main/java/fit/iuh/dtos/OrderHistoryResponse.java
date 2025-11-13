package fit.iuh.dtos;

import fit.iuh.models.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderHistoryResponse {
    private Long id;                // ID gốc
    private String orderCode;       // Mã hiển thị (VD: ORD-001)
    private LocalDate date;         // Ngày mua
    private OrderStatus status;     // Trạng thái
    private BigDecimal totalPrice;  // Tổng tiền
    private List<PurchasedGameDTO> games; // Danh sách game trong đơn

    @Data
    public static class PurchasedGameDTO {
        private Long gameId;
        private String gameName;
        private String publisherName;
        private String thumbnail;     // URL ảnh
        private String categoryName;  // Thể loại (Hành động, v.v.)
        private Integer requiredAge;  // Độ tuổi (18+)
    }
}