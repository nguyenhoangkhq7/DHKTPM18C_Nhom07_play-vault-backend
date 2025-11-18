package fit.iuh.services;

import fit.iuh.dtos.CartResponse;
import fit.iuh.models.*;
import fit.iuh.models.enums.OrderStatus;
import fit.iuh.repositories.CartRepository;
import fit.iuh.repositories.CustomerRepository;
import fit.iuh.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartService cartService; // Để tái sử dụng logic lấy CartResponse

    /**
     * Thanh toán các mục đã chọn trong giỏ hàng
     */
    @Transactional(rollbackFor = Exception.class)
    public CartResponse checkoutSelectedItems(String username, List<Long> cartItemIds) {
        // 1. Lấy thông tin Customer
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng: " + username));

        Cart cart = customer.getCart();
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống!");
        }

        // 2. Lọc ra các CartItem cần thanh toán
        List<CartItem> itemsToCheckout = cart.getCartItems().stream()
                .filter(item -> cartItemIds.contains(item.getId()))
                .collect(Collectors.toList());

        if (itemsToCheckout.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm nào để thanh toán trong giỏ hàng.");
        }

        // 3. Tính tổng tiền cần thanh toán
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : itemsToCheckout) {
            // Giá cuối cùng = Price - Discount
            BigDecimal finalPrice = item.getPrice().subtract(item.getDiscount());
            totalAmount = totalAmount.add(finalPrice);
        }

        // 4. Kiểm tra số dư tài khoản (Balance)
        BigDecimal currentBalance = customer.getBalance() != null ? customer.getBalance() : BigDecimal.ZERO;
        if (currentBalance.compareTo(totalAmount) < 0) {
            throw new RuntimeException("Số dư không đủ để thanh toán. Cần thêm: " +
                    totalAmount.subtract(currentBalance).toString() + " GCoin");
        }

        // 5. Trừ tiền của khách hàng
        customer.setBalance(currentBalance.subtract(totalAmount));
        customerRepository.save(customer);

        // 6. Tạo Order mới
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.COMPLETED); // Thanh toán thành công luôn
        order.setTotal(totalAmount);
        // createdAt được set tự động trong constructor của Order

        // 7. Chuyển CartItem -> OrderItem
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : itemsToCheckout) {
            OrderItem orderItem = new OrderItem();
            orderItem.setGame(cartItem.getGame());
            orderItem.setPrice(cartItem.getPrice());
            // Nếu có logic khuyến mãi phức tạp hơn thì set promotion ở đây
            // orderItem.setPromotion(...);

            // Tính toán total cho từng dòng item (để lưu vào DB)
            BigDecimal itemFinalPrice = cartItem.getPrice().subtract(cartItem.getDiscount());
            orderItem.setTotal(itemFinalPrice);

            // Liên kết 2 chiều
            order.addOrderItem(orderItem);
        }

        // Lưu Order (Cascade sẽ tự lưu OrderItems)
        orderRepository.save(order);

        // 8. Xóa các item đã mua khỏi Cart
        // Lưu ý: Vì orphanRemoval = true trong Cart entity,
        // việc xóa khỏi list cartItems sẽ tự động xóa row trong DB khi lưu Cart.
        cart.getCartItems().removeAll(itemsToCheckout);

        // Cập nhật lại tổng tiền của Cart (cho các món còn lại)
        BigDecimal remainingTotal = cart.getCartItems().stream()
                .map(item -> item.getPrice().subtract(item.getDiscount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(remainingTotal);

        cartRepository.save(cart);

        // 9. Trả về CartResponse mới nhất (đã trừ đi các món vừa mua)
        return cartService.getCartByUsername(username);
    }

    /**
     * Thanh toán toàn bộ giỏ hàng
     */
    @Transactional(rollbackFor = Exception.class)
    public CartResponse checkoutAllItems(String username) {
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = customer.getCart();
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống!");
        }

        // Lấy danh sách ID của TẤT CẢ item trong giỏ
        List<Long> allItemIds = cart.getCartItems().stream()
                .map(CartItem::getId)
                .collect(Collectors.toList());

        // Gọi lại hàm checkoutSelectedItems
        return checkoutSelectedItems(username, allItemIds);
    }
}