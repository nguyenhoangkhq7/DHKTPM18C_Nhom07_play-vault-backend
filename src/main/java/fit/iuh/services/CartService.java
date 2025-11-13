package fit.iuh.services;

import fit.iuh.dtos.CartItemResponse;
import fit.iuh.dtos.CartResponse;
import fit.iuh.mappers.CartMapper; // Import Mapper
import fit.iuh.models.*;
import fit.iuh.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final GameRepository gameRepository;
    private final CartMapper cartMapper;

    /**
     * Lấy thông tin giỏ hàng.
     */
    @Transactional
    public CartResponse getCartByUsername(String username) {
        // 1. Tìm Customer & Cart
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        Cart cart = customer.getCart();

        // 2. Auto-create Cart nếu chưa có
        if (cart == null) {
            cart = new Cart();
            cart.setTotalPrice(BigDecimal.ZERO);
            cartRepository.save(cart);

            customer.setCart(cart);
            customerRepository.save(customer);
        }

        // 3. Lấy danh sách CartItem từ DB
        List<CartItem> myItems = cartItemRepository.findByCartId(cart.getId());

        // 4. Dùng MapStruct chuyển đổi sang List DTO
        List<CartItemResponse> itemDTOs = cartMapper.toCartItemResponseList(myItems);

        // 5. Tính tổng tiền dựa trên List DTO vừa map xong
        BigDecimal calculatedTotal = itemDTOs.stream()
                .map(CartItemResponse::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 6. Cập nhật tổng tiền vào DB nếu lệch
        if (cart.getTotalPrice() == null || !cart.getTotalPrice().equals(calculatedTotal)) {
            cart.setTotalPrice(calculatedTotal);
            cartRepository.save(cart);
        }

        // 7. Trả về kết quả cuối cùng qua Mapper
        return cartMapper.toCartResponse(cart, itemDTOs, calculatedTotal);
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    @Transactional
    public void removeCartItem(String username, Long cartItemId) {
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (customer.getCart() == null) return;

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart Item not found"));

        if (!item.getCart().getId().equals(customer.getCart().getId())) {
            throw new RuntimeException("Unauthorized: You cannot remove items from another user's cart");
        }

        cartItemRepository.delete(item);
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    @Transactional
    public void addToCart(String username, Long gameId) {
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = customer.getCart();

        if (cart == null) {
            cart = new Cart();
            cart.setTotalPrice(BigDecimal.ZERO);
            cartRepository.save(cart);
            customer.setCart(cart);
            customerRepository.save(customer);
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with ID: " + gameId));

        // Tạo CartItem mới
        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setGame(game);

        if (game.getGameBasicInfos() != null) {
            newItem.setPrice(game.getGameBasicInfos().getPrice());
        } else {
            newItem.setPrice(BigDecimal.ZERO);
        }

        newItem.setDiscount(BigDecimal.ZERO);

        cartItemRepository.save(newItem);
    }
}