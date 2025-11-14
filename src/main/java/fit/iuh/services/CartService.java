package fit.iuh.services;

import fit.iuh.dtos.*;
import fit.iuh.mappers.CartMapper;
import fit.iuh.models.*;
import fit.iuh.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    // === Repositories ===
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final GameRepository gameRepository;
    private final CustomerRepository customerRepository;

    // === Mapper ===
    private final CartMapper cartMapper;

    // ========================================================================
    // 1. LẤY GIỎ HÀNG CHI TIẾT (DÙNG CHO API TRẢ VỀ RESPONSE ĐẦY ĐỦ)
    // ========================================================================
    public CartResponse getCartResponse(String username) {
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng: " + username));

        Cart cart = customer.getCart();
        if (cart == null) {
            cart = createNewCartForCustomer(customer);
        }

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        List<CartItemResponse> itemResponses = cartMapper.toCartItemResponseList(cartItems);

        BigDecimal totalPrice = itemResponses.stream()
                .map(CartItemResponse::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Cập nhật tổng tiền nếu cần
        if (!totalPrice.equals(cart.getTotalPrice())) {
            cart.setTotalPrice(totalPrice);
            cartRepository.save(cart);
        }

        return cartMapper.toCartResponse(cart, itemResponses, totalPrice);
    }

    // ========================================================================
    // 2. LẤY GIỎ HÀNG ĐƠN GIẢN (DÙNG CHO DTO NHẸ, KHÔNG CẦN TÍNH TOÁN)
    // ========================================================================
    public CartDto getCartDto(String username) {
        Cart cart = findOrCreateCartByUsername(username);
        CartDto cartDto = cartMapper.toDto(cart);

        BigDecimal totalPrice = cartDto.getItems().stream()
                .map(CartItemDto::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cartDto.setTotalPrice(totalPrice);
        return cartDto;
    }

    // ========================================================================
    // 3. THÊM GAME VÀO GIỎ HÀNG
    // ========================================================================
    @Transactional
    public CartResponse addGameToCart(String username, Long gameId) {
        Cart cart = findOrCreateCartByUsername(username);

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Game ID: " + gameId));

        // Kiểm tra trùng
        boolean exists = cartItemRepository.findByCartIdAndGameId(cart.getId(), gameId).isPresent();
        if (exists) {
            throw new RuntimeException("Game này đã có trong giỏ hàng.");
        }

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setGame(game);
        item.setPrice(game.getGameBasicInfos() != null ? game.getGameBasicInfos().getPrice() : BigDecimal.ZERO);
        item.setDiscount(BigDecimal.ZERO);

        cartItemRepository.save(item);

        return getCartResponse(username); // Trả về response đầy đủ
    }

    // ========================================================================
    // 4. XÓA ITEM KHỎI GIỎ HÀNG
    // ========================================================================
    @Transactional
    public CartResponse removeGameFromCart(String username, Long gameId) {
        Cart cart = findOrCreateCartByUsername(username);

        CartItem item = cartItemRepository.findByCartIdAndGameId(cart.getId(), gameId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy món hàng trong giỏ"));

        cartItemRepository.delete(item);

        return getCartResponse(username);
    }

    // ========================================================================
    // 5. XÓA TOÀN BỘ GIỎ HÀNG
    // ========================================================================
    @Transactional
    public CartResponse clearCart(String username) {
        Cart cart = findOrCreateCartByUsername(username);
        cart.getCartItems().clear(); // orphanRemoval = true → tự xóa trong DB
        cartRepository.save(cart);
        return getCartResponse(username);
    }

    // ========================================================================
    // HELPER: Tìm hoặc tạo giỏ hàng
    // ========================================================================
    private Cart findOrCreateCartByUsername(String username) {
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Customer: " + username));

        if (customer.getCart() != null) {
            return customer.getCart();
        }

        return createNewCartForCustomer(customer);
    }

    private Cart createNewCartForCustomer(Customer customer) {
        Cart newCart = new Cart();
        newCart.setTotalPrice(BigDecimal.ZERO);
        Cart savedCart = cartRepository.save(newCart);
        customer.setCart(savedCart);
        customerRepository.save(customer);
        return savedCart;
    }
}