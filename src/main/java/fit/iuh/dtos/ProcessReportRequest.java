package fit.iuh.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProcessReportRequest {
    @NotNull
    private Boolean approved; // true = Duyệt, false = Từ chối
    private String adminNote; // Ghi chú (VD: "Đã check VietQR khớp")
}