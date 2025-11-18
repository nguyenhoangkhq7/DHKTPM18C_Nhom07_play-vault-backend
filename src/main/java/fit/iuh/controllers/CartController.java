package fit.iuh.controllers;

import fit.iuh.dtos.CartDto;
import fit.iuh.dtos.CartItemRequestDto;
import fit.iuh.dtos.CartResponse;
import fit.iuh.services.CartService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.security.Principal; // Import để lấy thông tin user đã đăng nhập

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getMyCart() {
        // Lấy thông tin xác thực từ SecurityContextHolder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();

        return ResponseEntity.ok(cartService.getCartByUsername(username));
    }

    /**
     * Thêm sản phẩm vào giỏ hàng theo gameId
     */
    @PostMapping("/items/{gameId}")
    public ResponseEntity<CartResponse> addItemToCart(
            @PathVariable Long gameId
    ) { // Bỏ Principal principal
        // Lấy thông tin xác thực từ SecurityContextHolder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();

        cartService.addToCart(username, gameId);
        return ResponseEntity.ok(cartService.getCartByUsername(username));
    }

    /**
     * Xóa 1 item khỏi giỏ hàng bằng cartItemId
     */
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @PathVariable Long cartItemId
    ) {
        // Lấy thông tin xác thực từ SecurityContextHolder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();

        cartService.removeCartItem(username, cartItemId);
        return ResponseEntity.ok(cartService.getCartByUsername(username));
    }

    /**
     * Clear toàn bộ giỏ hàng
     */
    @DeleteMapping("/clear")
    public ResponseEntity<CartResponse> clearCart() { // Bỏ Principal principal
        // Lấy thông tin xác thực từ SecurityContextHolder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();

        cartService.clearCart(username);
        return ResponseEntity.ok(cartService.getCartByUsername(username));
    }


}
