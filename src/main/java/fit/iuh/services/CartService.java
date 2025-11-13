package fit.iuh.services;

import fit.iuh.models.Cart;
import fit.iuh.models.CartItem;
import fit.iuh.models.GameBasicInfo;
import fit.iuh.repositories.CartItemRepository;
import fit.iuh.repositories.CartRepository;
import fit.iuh.repositories.GameBasicInfoRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final GameBasicInfoRepository gameBasicRepo;


    public CartService(CartRepository cartRepo, CartItemRepository cartItemRepo, GameBasicInfoRepository gameRepo) {
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.gameBasicRepo = gameRepo;
    }

    //Lấy tất cả thông tin cơ bản của các game trong giỏ hàng của khách hàng theo customerId
    public List<GameBasicInfo> getGameBasicInfoByCustomerId(Long customerId) {
        return gameBasicRepo.findAllGameCartByCustomerId(customerId);
    }

    //Lấy tất cả các mục trong giỏ hàng của khách hàng theo customerId
    public List<CartItem> getCartItemByCustomerId(Long customerId) {
        return cartItemRepo.findAllGameItemByCustomerId(customerId);
    }



    //Thêm mục vào giỏ hàng
    public boolean addItem(CartItem cartItem) {
        try {
            cartItemRepo.save(cartItem);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Xóa mục khỏi giỏ hàng
    public boolean removeItem(CartItem cartItem) {
        try {
            cartItemRepo.delete(cartItem);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    //Xóa tất cả mục khỏi giỏ hàng
    public boolean clearAllItemInCart(long cartId, Long customerId) {
        try {
            cartItemRepo.deleteAll(getCartItemByCustomerId(customerId));
            Cart cart = cartRepo.findById(cartId).orElse(null);
            assert cart != null;
            cart.setTotalPrice(BigDecimal.ZERO);
            cartRepo.save(cart);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



}
