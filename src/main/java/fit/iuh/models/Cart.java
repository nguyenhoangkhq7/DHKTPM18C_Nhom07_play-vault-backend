package fit.iuh.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ColumnDefault("0.00")
    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;


    @OneToMany(
            mappedBy = "cart", // "cart" là tên trường Cart trong class CartItem
            cascade = CascadeType.ALL, // Lưu/Xóa Cart thì tự động Lưu/Xóa CartItem
            orphanRemoval = true // Xóa CartItem khỏi list thì tự động xóa trong DB
    )
    private List<CartItem> cartItems = new ArrayList<>(); // Khởi tạo list

}