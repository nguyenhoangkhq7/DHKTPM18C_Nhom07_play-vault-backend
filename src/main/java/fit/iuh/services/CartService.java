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
import java.util.Objects; // <-- THÊM IMPORT NÀY

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
    // 1. LẤY GIỎ HÀNG CHI TIẾT (API trả về CartResponse đầy đủ)
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

        // === SỬA DÒNG NÀY ===
        // Lọc ra các giá trị null trước khi cộng
        BigDecimal totalPrice = itemResponses.stream()
                .map(CartItemResponse::getFinalPrice)
                .filter(Objects::nonNull) // <-- THÊM DÒNG NÀY
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // === KẾT THÚC SỬA ===

        if (!totalPrice.equals(cart.getTotalPrice())) {
            cart.setTotalPrice(totalPrice);
            cartRepository.save(cart);
        }

        return cartMapper.toCartResponse(cart);
    }


    // ========================================================================
    // 2. LẤY GIỎ HÀNG CHUẨN (TỪ ĐOẠN 2) - giữ nguyên tên để Controller đang dùng
    // ========================================================================
    @Transactional
    public CartResponse getCartByUsername(String username) {
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        Cart cart = customer.getCart();

        if (cart == null) {
            cart = new Cart();
            cart.setTotalPrice(BigDecimal.ZERO);
            cartRepository.save(cart);

            customer.setCart(cart);
            customerRepository.save(customer);
        }

        List<CartItem> myItems = cartItemRepository.findByCartId(cart.getId());
        List<CartItemResponse> itemDTOs = cartMapper.toCartItemResponseList(myItems);

        // === SỬA DÒNG NÀY ===
        // Lọc ra các giá trị null trước khi cộng
        BigDecimal calculatedTotal = itemDTOs.stream()
                .map(CartItemResponse::getFinalPrice)
                .filter(Objects::nonNull) // <-- THÊM DÒNG NÀY
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // === KẾT THÚC SỬA ===

        // Cập nhật nếu lệch
        if (cart.getTotalPrice() == null || !cart.getTotalPrice().equals(calculatedTotal)) {
            cart.setTotalPrice(calculatedTotal);
            cartRepository.save(cart);
        }

        return cartMapper.toCartResponse(cart);
    }
    @Transactional
    public void addToCart(String username, Long gameId) {
        Cart cart = findOrCreateCartByUsername(username);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        // Lấy giá gốc
        BigDecimal basePrice = game.getGameBasicInfos() != null ?
                game.getGameBasicInfos().getPrice() : BigDecimal.ZERO;

        // 🎯 LOGIC TÍNH DISCOUNT AMOUNT 🎯
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (game.getPromotion() != null) {
            // Sử dụng phương thức tính toán từ entity Promotion
            discountAmount = game.getPromotion().calculateDiscount(basePrice);
        }

        // --------------------------------------------------------------------
        // KIỂM TRA: Game này đã tồn tại trong giỏ chưa? (Thường giỏ hàng game không cho thêm trùng)
        // Nếu bạn muốn đảm bảo không thêm trùng:
        // cartItemRepository.findByCartAndGame(cart, game).ifPresent(item -> {
        //      throw new RuntimeException("Game đã có trong giỏ hàng.");
        // });
        // Nếu code hiện tại của bạn là cho phép thêm trùng: (vì không có check exists)

        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setGame(game);
        newItem.setPrice(basePrice);

        // 🎯 Gán số tiền giảm giá đã tính toán 🎯
        newItem.setDiscount(discountAmount);

        cartItemRepository.save(newItem);
    }


    // ========================================================================
    // 3. THÊM GAME VÀO GIỎ HÀNG (từ đoạn 1)
    // ========================================================================
//    @Transactional
//    public CartResponse addGameToCart(String username, Long gameId) {
//        Cart cart = findOrCreateCartByUsername(username);
//
//        Game game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Game ID: " + gameId));
//
//        boolean exists = cartItemRepository.findByCartIdAndGameId(cart.getId(), gameId).isPresent();
//        if (exists) {
//            throw new RuntimeException("Game này đã có trong giỏ hàng.");
//        }
//
//        CartItem item = new CartItem();
//        item.setCart(cart);
//        item.setGame(game);
//        item.setPrice(game.getGameBasicInfos() != null ? game.getGameBasicInfos().getPrice() : BigDecimal.ZERO);
//        item.setDiscount(BigDecimal.ZERO);
//
//        cartItemRepository.save(item);
//
//        return getCartResponse(username);
//    }


    // ========================================================================
    // 4. THÊM GAME VÀO GIỎ (phiên bản từ đoạn 2 – giữ lại vì Controller đang dùng)
    // ========================================================================
//    @Transactional
//    public void addToCart(String username, Long gameId) {
//        Cart cart = findOrCreateCartByUsername(username);
//        Game game = gameRepository.findById(gameId)
//                .orElseThrow(() -> new RuntimeException("Game not found"));
//        CartItem newItem = new CartItem();
//        newItem.setCart(cart);
//        newItem.setGame(game);
//        newItem.setPrice(game.getGameBasicInfos() != null ? game.getGameBasicInfos().getPrice() : BigDecimal.ZERO);
//        newItem.setDiscount(BigDecimal.ZERO);
//        cartItemRepository.save(newItem);
//    }



    // ========================================================================
    // 5. XÓA GAME THEO gameId (đoạn 1)
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
    // 6. XÓA ITEM THEO cartItemId (từ đoạn 2)
    // ========================================================================
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


    // ========================================================================
    // 7. XÓA TOÀN BỘ GIỎ HÀNG
    // ========================================================================
    @Transactional
    public CartResponse clearCart(String username) {
        Cart cart = findOrCreateCartByUsername(username);
        // Sửa lại logic clear:
        // cart.getCartItems().clear() không xóa trong DB nếu không cấu hình orphanRemoval
        // Cách an toàn nhất là xóa bằng repo:
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        cartItemRepository.deleteAll(items);

        // Cập nhật lại tổng tiền trong cart
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);

        return getCartResponse(username);
    }


    // ========================================================================
    // 8. HELPER – Tìm hoặc tạo giỏ hàng
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