package fit.iuh.dtos;// package fit.iuh.dtos;
import lombok.Data;

@Data
public class CartItemRequestDto { // <-- DTO mới (để nhận yêu cầu)
    private Long gameId;
    private int quantity;
}