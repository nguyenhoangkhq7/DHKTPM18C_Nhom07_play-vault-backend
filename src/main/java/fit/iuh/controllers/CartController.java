package fit.iuh.controllers;

import fit.iuh.dtos.CartDto;
import fit.iuh.dtos.CartItemRequestDto;
import fit.iuh.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal; // Import để lấy thông tin user đã đăng nhập

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * API Lấy giỏ hàng CỦA TÔI (tự động lấy username đã đăng nhập)
     */
    @GetMapping
    public ResponseEntity<CartDto> getMyCart(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(cartService.getCartByUsername(username));
    }

    /**
     * API Thêm món hàng vào giỏ (tự động lấy username)
     */
    @PostMapping("/items")
    public ResponseEntity<CartDto> addItemToCart(@RequestBody CartItemRequestDto request, Principal principal) {
        String username = principal.getName();
        CartDto updatedCart = cartService.addItemToCart(username, request);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * API Xóa món hàng khỏi giỏ (tự động lấy username)
     */
    @DeleteMapping("/items/{gameId}")
    public ResponseEntity<CartDto> removeItemFromCart(@PathVariable Long gameId, Principal principal) {
        String username = principal.getName();
        CartDto updatedCart = cartService.removeItemFromCart(username, gameId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items")
    public ResponseEntity<CartDto> clearMyCart(Principal principal) {
        String username = principal.getName();
        CartDto updatedCart = cartService.clearCart(username);
        return ResponseEntity.ok(updatedCart);
    }
}