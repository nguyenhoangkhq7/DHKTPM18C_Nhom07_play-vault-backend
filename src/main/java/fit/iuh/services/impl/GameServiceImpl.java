package fit.iuh.services.impl;

import fit.iuh.dtos.GameCreateRequest;
import fit.iuh.dtos.GameDto;
import fit.iuh.dtos.GameSearchResponseDto;
import fit.iuh.dtos.GameWithRatingDto;
import fit.iuh.mappers.GameMapper;
import fit.iuh.models.*;
import fit.iuh.models.enums.SubmissionStatus;
import fit.iuh.repositories.*;
import fit.iuh.services.GameService;
import fit.iuh.specifications.GameSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper; // MapStruct
    private final CustomerRepository customerRepository; // THÊM REPOSITORY CẦN THIẾT

    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;
    private final PlatformRepository platformRepository;
    // Nếu bạn lưu GameBasicInfo qua cascade từ Game thì KHÔNG cần repo này.
    // Nếu bạn lưu riêng, hãy khai báo:
    private final GameBasicInfoRepository gameBasicInfoRepository;
    // ========================================================================
    // 1. TÌM KIẾM & LỌC NÂNG CAO (Specification + Pagination)
    // ========================================================================
    @Transactional(readOnly = true)
    public Page<GameSearchResponseDto> searchAndFilterGames(
            String keyword,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Pageable pageable) {

        BigDecimal minPriceBd = minPrice != null ? BigDecimal.valueOf(minPrice) : null;
        BigDecimal maxPriceBd = maxPrice != null ? BigDecimal.valueOf(maxPrice) : null;

        Specification<Game> spec = GameSpecification.filterBy(keyword, categoryId, minPriceBd, maxPriceBd);

        // Lấy danh sách Game từ DB
        Page<Game> gamePage = gameRepository.findAll(spec, pageable);

        // Map sang DTO (Lúc này logic tính toán Rating trong DTO sẽ chạy)
        return gamePage.map(GameSearchResponseDto::fromEntity);
    }

    // ========================================================================
    // 2. CÁC PHƯƠNG THỨC CƠ BẢN
    // ========================================================================
    @Override
    @Transactional(readOnly = true)
    public List<GameDto> findAll() {
        return gameRepository.findAll()
                .stream()
                .map(gameMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GameDto findById(Long id) {
        return gameRepository.findById(id)
                .map(gameMapper::toDTO)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDto> findGamesByCategoryName(String categoryName) {
        List<Game> games = (categoryName == null || categoryName.isBlank())
                ? gameRepository.findAll()
                : gameRepository.findByGameBasicInfos_Category_Name(categoryName);

        return games.stream()
                .map(gameMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDto> findTopRatedGames(int topN) {
        return gameRepository.findTopRatedGames(topN)
                .stream()
                .map(gameMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Game findGameEntityById(Long id) {
        // Trả về nguyên con Entity Game lấy từ DB
        return gameRepository.findById(id).orElse(null);
    }
    public List<GameWithRatingDto> getTopGamesWithRating(int topN) {
        List<Game> allGames = gameRepository.findAll();
        if(topN==0){
            return gameMapper.toGameWithRatingDtoList(allGames)
                    .stream()
                    .sorted(Comparator.comparing(GameWithRatingDto::getAvgRating).reversed())
                    .collect(Collectors.toList());
        }
        return gameMapper.toGameWithRatingDtoList(allGames)
                .stream()
                .sorted(Comparator.comparing(GameWithRatingDto::getAvgRating).reversed())
                .limit(topN)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public GameWithRatingDto getGameWithRatingById(Long id) {
        Game game = gameRepository.findById(id).orElse(null);

        if (game == null) {
            return null;
        }
        return gameMapper.toGameWithRatingDto(game);
    }

    // ========================================================================
    // 3. THÊM: LOGIC KIỂM TRA QUYỀN SỞ HỮU (Buy & Download)
    // ========================================================================


    @Override
    @Transactional
    public GameDto updateStatus(Long id, String status) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game không tồn tại"));
        try {
            SubmissionStatus s = SubmissionStatus.valueOf(status.trim().toUpperCase());
            game.setStatus(s);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ (PENDING|APPROVED|REJECTED)");
        }
        game = gameRepository.save(game);
        return gameMapper.toDTO(game);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkOwnership(String username, Long gameId) {
        // TODO: thay bằng logic của bạn nếu khác
        return customerRepository
                .existsByAccount_UsernameAndOwnedGames_Id(username, gameId);
    }

    @Override
    @Transactional
    public GameDto createPending(GameCreateRequest req, String publisherUsername) {

        // Tìm Publisher theo email
        Publisher publisher = publisherRepository.findByAccount_Username(publisherUsername)
                .orElseThrow(() -> new RuntimeException("Publisher không tồn tại"));

        // 1️⃣ Tạo GameBasicInfo
        GameBasicInfo info = new GameBasicInfo();
        info.setName(req.getTitle());
        info.setShortDescription(req.getSummary());
        info.setDescription(req.getDescription());
        info.setThumbnail(req.getCoverUrl());
        info.setTrailerUrl(req.getTrailerUrl());
        info.setPrice(BigDecimal.valueOf(req.isFree() ? 0.0 : req.getPrice()));
        info.setIsSupportController(req.isSupportController());
        info.setRequiredAge(req.isAge18() ? 18 : 0);
        info.setPublisher(publisher);
        info.setFilePath(req.getFilePath());
        System.out.println("📁 File path nhận được từ frontend: " + req.getFilePath());


        // Category
        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy category"));
            info.setCategory(category);
        }

        // Lưu trước info (để có id)
        info = gameBasicInfoRepository.save(info);

        // 2️⃣ Lưu platforms
        var platformEntities = new java.util.ArrayList<Platform>();
        for (String name : req.getPlatforms()) {
            platformEntities.add(
                    platformRepository.findByName(name.toUpperCase())
                            .orElseThrow(() -> new RuntimeException("Platform không hợp lệ: " + name))
            );
        }
        info.setPlatforms(platformEntities);

        // 3️⃣ Tạo Game (bản chính)
        Game game = new Game();
        game.setGameBasicInfos(info);
        game.setReleaseDate(req.getReleaseDate());
        game.setStatus(SubmissionStatus.PENDING);

        // Lưu vào DB
        game = gameRepository.save(game);

        return gameMapper.toDTO(game);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDto> findByStatus(String status) {
        List<Game> games = gameRepository.findByStatus(status); // ✅ cần repo
        return games.stream().map(gameMapper::toDTO).toList();
    }


}