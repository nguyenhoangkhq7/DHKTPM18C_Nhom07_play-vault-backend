package fit.iuh.models;

import fit.iuh.models.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private OrderStatus status;

    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    // ========================================================================
    // 1. DANH SÁCH ORDER ITEM
    // ========================================================================
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<OrderItem> orderItems = new ArrayList<>(); // Khởi tạo để tránh NPE


    // ========================================================================
    // 2. CONSTRUCTORS
    // ========================================================================
    public Order() {
        this.createdAt = LocalDate.now();
        this.status = OrderStatus.PENDING;
        this.total = BigDecimal.ZERO;
        this.orderItems = new ArrayList<>();
    }


    // ========================================================================
    // 3. BUSINESS METHODS
    // ========================================================================

    /**
     * Tính tổng tiền đơn hàng từ danh sách OrderItem
     * @return tổng tiền sau khi cập nhật
     */
    public BigDecimal calculateTotal() {
        if (orderItems == null || orderItems.isEmpty()) {
            this.total = BigDecimal.ZERO;
            return this.total;
        }

        this.total = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return this.total;
    }

    /**
     * Hoàn tất đơn hàng
     */
    public void completeOrder() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Không thể hoàn tất đơn hàng đã bị hủy.");
        }
        if (this.status == OrderStatus.COMPLETED) {
            return; // Đã hoàn tất rồi
        }
        this.status = OrderStatus.COMPLETED;
        calculateTotal(); // Đảm bảo total chính xác trước khi hoàn tất
    }

    /**
     * Hủy đơn hàng
     */
    public void cancelOrder() {
        if (this.status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Không thể hủy đơn hàng đã hoàn tất.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    // ========================================================================
    // 4. HELPER METHODS (Tùy chọn - hỗ trợ thêm item)
    // ========================================================================
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }
}