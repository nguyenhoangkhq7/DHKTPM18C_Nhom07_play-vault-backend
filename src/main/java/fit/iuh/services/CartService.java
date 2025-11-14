package fit.iuh.services;

import fit.iuh.dtos.*;
import fit.iuh.mappers.CartMapper; // MapStruct Mapper
import fit.iuh.models.*;
import fit.iuh.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartService {

    // Repositories
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final GameRepository gameRepository;
    private final CustomerRepository customerRepository;

    // MapStruct Mapper
    private final CartMapper cartMapper;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       GameRepository gameRepository,
                       CustomerRepository customerRepository,
                       CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.gameRepository = gameRepository;
        this.customerRepository = customerRepository;
        this.cartMapper = cartMapper;
    }

    /**
     * Lấy giỏ hàng (ĐÃ CẬP NHẬT LOGIC TÍNH TỔNG)
     * Hoạt động chính xác với CartItemDto (không có quantity)
     */
    public CartDto getCartByUsername(String username) {
        Cart cart = findOrCreateCartByUsername(username);
        CartDto cartDto = cartMapper.toDto(cart);

        // --- Logic Service: Tính tổng tiền (totalPrice) ---
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItemDto itemDto : cartDto.getItems()) {
            // Cộng thẳng giá cuối của từng item (vì không có số lượng)
            totalPrice = totalPrice.add(itemDto.getFinalPrice());
        }
        cartDto.setTotalPrice(totalPrice);

        return cartDto;
    }

    /**
     * Thêm món hàng (ĐÃ CẬP NHẬT LOGIC)
     * Hoạt động chính xác với CartItemDto (không có quantity)
     */
    public CartDto addItemToCart(String username, CartItemRequestDto request) {
        Cart cart = findOrCreateCartByUsername(username);
        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Game"));

        // 1. Kiểm tra xem game đã có trong giỏ chưa
        boolean itemExists = cartItemRepository.findByCartIdAndGameId(cart.getId(), game.getId()).isPresent();

        if (itemExists) {
            throw new RuntimeException("Game này đã có trong giỏ hàng của bạn.");
        }

        // 2. Nếu chưa có, tạo item mới
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setGame(game);
        // (Không set quantity, hoặc set 1 nếu DB yêu cầu)
        // item.setQuantity(1);

        cartItemRepository.save(item);

        return getCartByUsername(username); // Trả về giỏ hàng đã cập nhật
    }

    /**
     * Xóa món hàng khỏi giỏ
     */
    public CartDto removeItemFromCart(String username, Long gameId) {
        Cart cart = findOrCreateCartByUsername(username);
        CartItem item = cartItemRepository.findByCartIdAndGameId(cart.getId(), gameId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy món hàng trong giỏ"));

        cartItemRepository.delete(item);

        return getCartByUsername(username);
    }

    //--- Helper Method (Giữ nguyên) ---
    private Cart findOrCreateCartByUsername(String username) {
        Customer customer = customerRepository.findByAccountUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Customer cho account: " + username));

        if (customer.getCart() != null) {
            return customer.getCart();
        }

        Cart newCart = new Cart();
        Cart savedCart = cartRepository.save(newCart);

        customer.setCart(savedCart);
        customerRepository.save(customer);

        return savedCart;
    }
    // ... (bên trong class CartService)

    /**
     * Xóa TẤT CẢ món hàng khỏi giỏ
     */
    public CartDto clearCart(String username) {
        // 1. Tìm giỏ hàng của user
        Cart cart = findOrCreateCartByUsername(username);

        // 2. Xóa tất cả item trong list
        // Nhờ "orphanRemoval = true" trên Entity Cart,
        // JPA sẽ tự động xóa các CartItem này khỏi CSDL.
        cart.getCartItems().clear();

        // 3. (Không bắt buộc, nhưng rõ ràng) Lưu giỏ hàng đã bị xóa item
        cartRepository.save(cart);

        // 4. Trả về DTO giỏ hàng (lúc này đã trống)
        return getCartByUsername(username);
    }

// ... (giữ nguyên các phương thức khác)
}