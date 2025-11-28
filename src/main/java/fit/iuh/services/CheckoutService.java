package fit.iuh.services;

import fit.iuh.dtos.CartResponse;
import fit.iuh.dtos.CheckoutResponseDto;
import fit.iuh.models.*;
import fit.iuh.models.enums.OrderStatus;
import fit.iuh.repositories.*;
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
    private final CartService cartService;

    /**
     * Thanh toán các mục đã chọn
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckoutResponseDto checkoutSelectedItems(String username, List<Long> cartItemIds) {
        return processCheckout(username, cartItemIds);
    }

    /**
     * Thanh toán toàn bộ giỏ hàng
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckoutResponseDto checkoutAllItems(String username) {
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Cart cart = customer.getCart();
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống!");
        }

        List<Long> allItemIds = cart.getCartItems().stream()
                .map(CartItem::getId)
                .collect(Collectors.toList());

        return processCheckout(username, allItemIds);
    }

    // HÀM CHUNG XỬ LÝ THANH TOÁN (tái sử dụng)
    private CheckoutResponseDto processCheckout(String username, List<Long> cartItemIds) {
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        Cart cart = customer.getCart();
        if (cart == null) {
            throw new RuntimeException("Giỏ hàng không tồn tại");
        }

        // Lọc các item cần thanh toán
        List<CartItem> itemsToCheckout = cart.getCartItems().stream()
                .filter(item -> cartItemIds.contains(item.getId()))
                .collect(Collectors.toList());

        if (itemsToCheckout.isEmpty()) {
            return new CheckoutResponseDto(false, "Không có sản phẩm nào để thanh toán",
                    customer.getBalance().longValue(), null);
        }

        // Tính tổng tiền
        BigDecimal totalAmount = itemsToCheckout.stream()
                .map(item -> item.getPrice().subtract(item.getDiscount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Kiểm tra số dư
        BigDecimal currentBalance = customer.getBalance() != null ? customer.getBalance() : BigDecimal.ZERO;
        if (currentBalance.compareTo(totalAmount) < 0) {
            return new CheckoutResponseDto(false,
                    "Số dư không đủ! Cần thêm " + totalAmount.subtract(currentBalance) + " GCoin",
                    currentBalance.longValue(), null);
        }

        // TRỪ TIỀN
        customer.setBalance(currentBalance.subtract(totalAmount));
        customerRepository.save(customer);

        System.out.println("New balance: " + customer.getBalance());

        // Tạo Order
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.COMPLETED);
        order.setTotal(totalAmount);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : itemsToCheckout) {
            OrderItem orderItem = new OrderItem();
            orderItem.setGame(cartItem.getGame());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setTotal(cartItem.getPrice().subtract(cartItem.getDiscount()));
            order.addOrderItem(orderItem);
            orderItems.add(orderItem);
        }
        orderRepository.save(order);
        // Thêm game vào library
        for (OrderItem item : order.getOrderItems()) {
            if (item.getGame() != null && !customer.getLibrary().contains(item.getGame())) {
                customer.getLibrary().add(item.getGame());
            }
        }
        // Lưu customer với library mới
        customerRepository.save(customer);

        // XÓA KHỎI GIỎ HÀNG
        cart.getCartItems().removeAll(itemsToCheckout);

        // Cập nhật totalPrice của cart
        BigDecimal remainingTotal = cart.getCartItems().stream()
                .map(item -> item.getPrice().subtract(item.getDiscount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(remainingTotal);
        cartRepository.save(cart);

        // Lấy cart mới nhất
        CartResponse updatedCart = cartService.getCartByUsername(username);
        System.out.println("New balance: " + customer.getBalance());

        // TRẢ VỀ KẾT QUẢ HOÀN HẢO
        return new CheckoutResponseDto(
                true,
                "Thanh toán thành công! Đã trừ " + totalAmount + " GCoin",
                customer.getBalance().longValue(),  // ← newBalance ở đây
                updatedCart
        );

    }
}