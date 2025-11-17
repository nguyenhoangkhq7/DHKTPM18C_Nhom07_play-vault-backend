package fit.iuh.services;

import fit.iuh.dtos.GameBasicInfoDto;
import fit.iuh.mappers.GameMapper;
import fit.iuh.models.Account;
import fit.iuh.models.Customer;
import fit.iuh.models.Game;
import fit.iuh.repositories.AccountRepository;
import fit.iuh.repositories.CustomerRepository;
import fit.iuh.repositories.GameBasicInfoRepository;
import fit.iuh.repositories.GameRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository; // Cần Customer
    private final GameRepository gameRepository; // Cần Game
    private final GameBasicInfoRepository gameBasicInfoRepository;
    private final GameMapper gameMapper; // <-- Sử dụng MapStruct Mapper

    /**
     * Hàm trợ giúp: Tìm Customer qua Username
     */
    private Customer findCustomer(String username) {
        Account account = accountRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("Account not found: " + username));
        
        // Giả định Customer liên kết với Account
        return customerRepository.findByAccount(account)
                .orElseThrow(() -> new EntityNotFoundException("Customer profile not found"));
    }

    /**
     * LẤY danh sách game ưa thích
     */
    public List<GameBasicInfoDto> getWishlist(String username) {

        
        // **DÙNG MAPSTRUCT ĐỂ CONVERT**
        // Chuyển Set<Game> (từ Customer) -> List<GameBasicInfoDto> (DTO)
        return gameMapper.toDtoList(gameBasicInfoRepository.findAllByGameFavoriteWithCustomerId(username));
    }

    /**
     * THÊM game vào danh sách
     */
    public void addGameToWishlist(String username, Long gameId) {
        Customer customer = findCustomer(username);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found"));

        customer.getWishlist().add(game); // Logic thêm
    }

    /**
     * XÓA game khỏi danh sách
     */
    public void removeGameFromWishlist(String username, Long gameId) {
        Customer customer = findCustomer(username);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found"));

        customer.getWishlist().remove(game); // Logic xóa
    }
}