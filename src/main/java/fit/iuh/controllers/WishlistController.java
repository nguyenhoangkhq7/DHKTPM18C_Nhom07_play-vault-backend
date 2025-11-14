package fit.iuh.controllers;

import fit.iuh.dtos.GameBasicInfoDto;
import fit.iuh.services.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal; // Dùng để lấy user đã đăng nhập
import java.util.List;

@RestController
@RequestMapping("/api/wishlist") // (Hoặc URL bạn muốn)
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * Lấy danh sách game ưa thích của user
     */
    @GetMapping
    public ResponseEntity<List<GameBasicInfoDto>> getMyWishlist(Principal principal) {
        // principal.getName() sẽ là username (nếu dùng Spring Security)
        return ResponseEntity.ok(wishlistService.getWishlist(principal.getName()));
    }

    /**
     * Thêm một game vào danh sách ưa thích
     */
    @PostMapping("/{gameId}")
    public ResponseEntity<Void> addGame(@PathVariable Long gameId, Principal principal) {
        wishlistService.addGameToWishlist(principal.getName(), gameId);
        return ResponseEntity.ok().build();
    }

    /**
     * Xóa một game khỏi danh sách ưa thích
     */
    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> removeGame(@PathVariable Long gameId, Principal principal) {
        wishlistService.removeGameFromWishlist(principal.getName(), gameId);
        return ResponseEntity.ok().build();
    }
}