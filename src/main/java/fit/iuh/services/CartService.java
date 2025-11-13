package fit.iuh.services;

import fit.iuh.dtos.CartItemResponse;
import fit.iuh.dtos.CartResponse;
import fit.iuh.models.*;
import fit.iuh.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GameRepository gameRepository;

    /**
     * Lấy thông tin giỏ hàng.
     * Nếu khách hàng cũ chưa có giỏ hàng -> Tự động tạo mới.
     */
    @Transactional
    public CartResponse getCartByUsername(String username) {
        // 1. Tìm Customer
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        Cart cart = customer.getCart();

        // 2. [FIX LỖI NULL POINTER] Kiểm tra nếu chưa có Cart thì tạo ngay
        if (cart == null) {
            cart = new Cart();
            cart.setTotalPrice(BigDecimal.ZERO);
            cartRepository.save(cart); // Lưu xuống DB để sinh ID

            customer.setCart(cart);    // Gán Cart mới cho Customer
            customerRepository.save(customer); // Cập nhật Customer
        }

        // 3. Lấy danh sách sản phẩm trong giỏ
        // (Lưu ý: Bạn cần có method findByCartId trong CartItemRepository)
        List<CartItem> myItems = cartItemRepository.findByCartId(cart.getId());

        // 4. Tính toán và map sang DTO để trả về UI
        List<CartItemResponse> itemDTOs = new ArrayList<>();
        BigDecimal calculatedTotal = BigDecimal.ZERO;

        for (CartItem item : myItems) {
            CartItemResponse dto = new CartItemResponse();
            dto.setCartItemId(item.getId());

            // Lấy thông tin game
            Game game = item.getGame();
            dto.setGameId(game.getId());

            // Lấy thông tin cơ bản (Tên, Hình, Giá) từ GameBasicInfo
            if (game.getGameBasicInfos() != null) {
                GameBasicInfo info = game.getGameBasicInfos();
                dto.setGameName(info.getName());
                dto.setThumbnail(info.getThumbnail());

                // Xử lý giá
                BigDecimal originalPrice = item.getPrice(); // Giá lúc thêm vào
                BigDecimal discount = item.getDiscount() == null ? BigDecimal.ZERO : item.getDiscount();
                BigDecimal finalPrice = originalPrice.subtract(discount);

                dto.setOriginalPrice(originalPrice);
                dto.setFinalPrice(finalPrice);

                calculatedTotal = calculatedTotal.add(finalPrice);
            }

            itemDTOs.add(dto);
        }

        // 5. Cập nhật lại tổng tiền vào Database nếu có sai lệch
        if (cart.getTotalPrice() == null || !cart.getTotalPrice().equals(calculatedTotal)) {
            cart.setTotalPrice(calculatedTotal);
            cartRepository.save(cart);
        }

        // 6. Tạo response hoàn chỉnh
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setItems(itemDTOs);
        response.setTotalItems(itemDTOs.size());
        response.setTotalPrice(calculatedTotal);

        return response;
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    @Transactional
    public void removeCartItem(String username, Long cartItemId) {
        // Lấy Customer để check quyền sở hữu
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Nếu customer chưa có cart thì ko thể xóa gì cả -> return
        if (customer.getCart() == null) return;

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart Item not found"));

        // Bảo mật: Check xem item này có thuộc về cart của user đang login không
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
        // Gọi hàm này để đảm bảo Cart luôn tồn tại (Logic tạo mới nằm ở getCartByUsername)
        // Tuy nhiên để tối ưu hơn, ta viết lại logic check ở đây để tránh query thừa
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = customer.getCart();

        // Logic tạo cart nếu null (Copy từ trên xuống cho chắc chắn)
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

        // Lấy giá hiện tại của Game để lưu vào CartItem
        if (game.getGameBasicInfos() != null) {
            newItem.setPrice(game.getGameBasicInfos().getPrice());
        } else {
            newItem.setPrice(BigDecimal.ZERO);
        }

        newItem.setDiscount(BigDecimal.ZERO); // Mặc định discount = 0

        cartItemRepository.save(newItem);
    }
}