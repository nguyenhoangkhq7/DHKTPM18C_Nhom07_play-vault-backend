package fit.iuh.services;

import fit.iuh.dtos.PromotionRequestDto;
import fit.iuh.dtos.PromotionResponseDto;
import fit.iuh.mappers.PromotionMapper;
import fit.iuh.models.Game;
import fit.iuh.models.Promotion;
import fit.iuh.models.Publisher;
import fit.iuh.repositories.GameRepository;
import fit.iuh.repositories.PromotionRepository;
import fit.iuh.repositories.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
// Thêm 2 import này vào đầu file
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PublisherRepository publisherRepository;
    private final GameRepository gameRepository;
    private final PromotionMapper promotionMapper;
    // Thêm method này vào class PromotionService
    public Page<PromotionResponseDto> searchMyPromotions(
            String username,
            String keyword,
            LocalDate fromDate,
            LocalDate toDate,
            String status,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());

        Page<Promotion> result = promotionRepository.searchMyPromotions(
                username,
                keyword == null ? null : keyword.trim(),
                fromDate,
                toDate,
                status == null || status.isBlank() ? "ALL" : status.toUpperCase(),
                pageable);

        return result.map(promotionMapper::toDto);
    }

    // 1. Lấy danh sách khuyến mãi của tôi
    public List<PromotionResponseDto> getMyPromotions(String username) {
        return promotionRepository.findByPublisher_Account_UsernameOrderByStartDateDesc(username)
                .stream()
                .map(promotionMapper::toDto)
                .collect(Collectors.toList());
    }

    // 2. Tạo khuyến mãi mới
    @Transactional
    public PromotionResponseDto createPromotion(String username, PromotionRequestDto request) {
        // Sửa ở đây: tìm Publisher theo username
        Publisher publisher = publisherRepository.findByAccount_Username(username)
                .orElseThrow(() -> new UsernameNotFoundException("Publisher not found"));

        Promotion promotion = promotionMapper.toEntity(request);
        promotion.setPublisher(publisher); // Gán chủ sở hữu

        Promotion saved = promotionRepository.save(promotion);
        return promotionMapper.toDto(saved);
    }

    // 3. Áp dụng khuyến mãi cho danh sách Game
    @Transactional
    public void applyPromotionToGames(Long promotionId, String username, List<Long> gameIds) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));

        if (!promotion.getPublisher().getAccount().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không có quyền sử dụng khuyến mãi này");
        }

        List<Game> gamesToApply = gameRepository.findAllById(gameIds);

        for (Game game : gamesToApply) {
            if (game.getGameBasicInfos() == null || game.getGameBasicInfos().getPublisher() == null) {
                throw new RuntimeException("Game hoặc Publisher chưa được khởi tạo: ID " + game.getId());
            }

            if (!game.getGameBasicInfos().getPublisher().getAccount().getUsername().equals(username)) {
                throw new RuntimeException("Bạn không thể áp dụng khuyến mãi cho game không phải của mình: ID " + game.getId());
            }

            game.setPromotion(promotion);
        }

        gameRepository.saveAll(gamesToApply);
    }

    // 4. Gỡ khuyến mãi khỏi Game
    @Transactional
    public void removePromotionFromGame(Long gameId, String username) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game không tồn tại"));

        if (game.getGameBasicInfos() == null || game.getGameBasicInfos().getPublisher() == null ||
                !game.getGameBasicInfos().getPublisher().getAccount().getUsername().equals(username)) {
            throw new RuntimeException("Không có quyền sửa game này");
        }

        game.setPromotion(null);
        gameRepository.save(game);
    }
}
