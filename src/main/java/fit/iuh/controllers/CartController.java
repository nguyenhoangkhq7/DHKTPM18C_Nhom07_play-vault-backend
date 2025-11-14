package fit.iuh.controllers;

import fit.iuh.dtos.CartResponse;
import fit.iuh.services.CartService;
import fit.iuh.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtService jwtService;

    // Helper method để lấy username từ Header
    private String getUsernameFromHeader(String authHeader) {
        // Cắt bỏ chữ "Bearer " để lấy token thô
        String token = authHeader.replace("Bearer ", "");
        // Gọi hàm chính xác trong JwtService của bạn
        return jwtService.getUsernameFromToken(token);
    }

    // GET: Xem giỏ hàng
    // URL: http://localhost:8080/api/cart
    @GetMapping
    public ResponseEntity<CartResponse> viewCart(@RequestHeader("Authorization") String authHeader) {
        String username = getUsernameFromHeader(authHeader);
        CartResponse cart = cartService.getCartByUsername(username);
        return ResponseEntity.ok(cart);
    }

    // DELETE: Xóa sản phẩm khỏi giỏ
    // URL: http://localhost:8080/api/cart/item/{id}
    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable Long cartItemId,
            @RequestHeader("Authorization") String authHeader) {

        String username = getUsernameFromHeader(authHeader);
        cartService.removeCartItem(username, cartItemId);

        // Trả về giỏ hàng mới nhất để UI cập nhật
        return ResponseEntity.ok(cartService.getCartByUsername(username));
    }

    // POST: Thêm sản phẩm vào giỏ (Dùng để test)
    // URL: http://localhost:8080/api/cart/add?gameId=1
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @RequestParam Long gameId,
            @RequestHeader("Authorization") String authHeader) {

        String username = getUsernameFromHeader(authHeader);
        cartService.addToCart(username, gameId);
        return ResponseEntity.ok("Added successfully");
    }
}