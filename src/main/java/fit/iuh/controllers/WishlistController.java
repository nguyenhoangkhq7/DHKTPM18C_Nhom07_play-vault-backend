package fit.iuh.controllers;

import fit.iuh.dtos.GameBasicInfoDto;
import fit.iuh.services.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<List<GameBasicInfoDto>> getMyWishlist() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();
        List<GameBasicInfoDto> wishlist = wishlistService.getWishlist(username);
        return ResponseEntity.ok(wishlist);
    }

    /**
     * Thêm một game vào danh sách ưa thích
     */
    @PostMapping("/{gameId}")
    public ResponseEntity<Void> addGame(@PathVariable Long gameId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();
        wishlistService.addGameToWishlist(username, gameId);
        return ResponseEntity.ok().build();
    }

    /**
     * Xóa một game khỏi danh sách ưa thích
     */
    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> removeGame(@PathVariable Long gameId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = auth.getName();
        wishlistService.removeGameFromWishlist(username, gameId);
        return ResponseEntity.ok().build();
    }
}