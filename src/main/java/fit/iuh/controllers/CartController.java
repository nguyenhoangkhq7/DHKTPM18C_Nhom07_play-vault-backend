package fit.iuh.controllers;

import fit.iuh.models.CartItem;
import fit.iuh.models.GameBasicInfo;
import fit.iuh.services.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Lấy tất cả thông tin cơ bản của các game trong giỏ hàng của khách hàng theo customerId
    @GetMapping("/{customerId}/games")
    public List<GameBasicInfo> getAllGameBasicInfoByCustomerId(@PathVariable Long customerId) {
        return cartService.getGameBasicInfoByCustomerId(customerId);
    }

    // Lấy tất cả các mục trong giỏ hàng của khách hàng theo customerId
    @GetMapping("/{customerId}/items")
    public List<CartItem> getCartItemByCustomerId(@PathVariable Long customerId) {
        return cartService.getCartItemByCustomerId(customerId);
    }

    // Thêm mục vào giỏ hàng
    @PostMapping("/add")
    public boolean addItem(@RequestBody CartItem cartItem) {
        return cartService.addItem(cartItem);
    }
    // Xóa mục khỏi giỏ hàng
    @DeleteMapping("/remove")
    public boolean removeItem(@RequestBody CartItem cartItem) {
        return cartService.removeItem(cartItem);
    }
    // Xóa tất cả mục khỏi giỏ hàng
    @DeleteMapping("/{cartId}/clear")
    public boolean clearAllItemInCart(@PathVariable long cartId, @RequestParam Long customerId) {
        return cartService.clearAllItemInCart(cartId, customerId);
    }
}