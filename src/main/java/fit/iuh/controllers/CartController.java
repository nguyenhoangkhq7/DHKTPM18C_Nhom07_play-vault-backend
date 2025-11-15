package fit.iuh.controllers;

import fit.iuh.dtos.CartDto;
import fit.iuh.dtos.CartItemRequestDto;
import fit.iuh.dtos.CartResponse;
import fit.iuh.services.CartService;
import org.springframework.http.ResponseEntity;
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

    /**
     * Lấy giỏ hàng hiện tại của user
     */
    @GetMapping
    public ResponseEntity<CartResponse> getMyCart(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(cartService.getCartByUsername(username));
    }

    /**
     * Thêm sản phẩm vào giỏ hàng theo gameId
     */
    @PostMapping("/items/{gameId}")
    public ResponseEntity<CartResponse> addItemToCart(
            @PathVariable Long gameId,
            Principal principal
    ) {
        String username = principal.getName();
        cartService.addToCart(username, gameId);
        return ResponseEntity.ok(cartService.getCartByUsername(username));
    }

    /**
     * Xóa 1 item khỏi giỏ hàng bằng cartItemId
     */
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @PathVariable Long cartItemId,
            Principal principal
    ) {
        String username = principal.getName();
        cartService.removeCartItem(username, cartItemId);
        return ResponseEntity.ok(cartService.getCartByUsername(username));
    }

    /**
     * Clear toàn bộ giỏ hàng
     */
    @DeleteMapping("/clear")
    public ResponseEntity<CartResponse> clearCart(Principal principal) {
        String username = principal.getName();
        cartService.clearCart(username); // bạn sẽ cần implement trong CartService
        return ResponseEntity.ok(cartService.getCartByUsername(username));
    }

    /**
     * API cập nhật số lượng item trong giỏ
     * (thường giỏ hàng cần API này)
     */
//    @PutMapping("/items/{cartItemId}/{quantity}")
//    public ResponseEntity<CartResponse> updateItemQuantity(
//            @PathVariable Long cartItemId,
//            @PathVariable int quantity,
//            Principal principal
//    ) {
//        String username = principal.getName();
//        cartService.updateQuantity(username, cartItemId, quantity); // cần implement
//        return ResponseEntity.ok(cartService.getCartByUsername(username));
//    }

}
