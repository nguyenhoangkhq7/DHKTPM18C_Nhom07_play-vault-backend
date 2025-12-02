package fit.iuh.services.impl;

import fit.iuh.dtos.*;
import fit.iuh.mappers.GameMapper;
import fit.iuh.mappers.PublisherMapper;
import fit.iuh.models.*;
import fit.iuh.models.enums.AccountStatus;
import fit.iuh.models.enums.RequestStatus;
import fit.iuh.models.enums.Role;
import fit.iuh.repositories.*;
import fit.iuh.services.PublisherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {

    // --- Dependencies từ nhánh vanhau ---
    private final AccountRepository accountRepository;
    private final PublisherRepository publisherRepository;
    private final PublisherRequestRepository publisherRequestRepository;
    private final PaymentInfoRepository paymentInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final GameRepository gameRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;
    private final CategoryRepository categoryRepository;
    private final GameMapper gameMapper; // Mapper tự viết hoặc dùng MapStruct

    // --- Dependencies từ nhánh main ---
    private final PublisherMapper publisherMapper;

    // ========================================================================
    // 1. CÁC PHƯƠNG THỨC TỪ NHÁNH MAIN (User/Common features)
    // ========================================================================
    @Override
    public List<PublisherDto> findAll() {
        return publisherMapper.toPublisherDTOs(publisherRepository.findAll());
    }

    // ========================================================================
    // 2. CÁC PHƯƠNG THỨC TỪ NHÁNH VANHAU (Publisher Dashboard & Logic)
    // ========================================================================
    @Override
    @Transactional
    public void registerPublisher(PublisherRegisterRequest request) {

        // 1. Kiểm tra username tồn tại chưa
        if (accountRepository.existsByUsername(request.getUserName())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        // 2. Tạo và Lưu Account (Status: LOCKED chờ duyệt)
        Account account = new Account();
        account.setUsername(request.getUserName());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setEmail(request.getEmail());
        account.setPhone(request.getPhone());
        account.setCreatedAt(LocalDate.now());
        account.setRole(Role.PUBLISHER);
        account.setStatus(AccountStatus.LOCKED);
        Account savedAccount = accountRepository.save(account);

        // 3. Tạo và Lưu PaymentInfo (MỚI)
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentMethod(request.getPaymentMethod());
        paymentInfo.setAccountName(request.getAccountName());
        paymentInfo.setAccountNumber(request.getAccountNumber());
        paymentInfo.setBankName(request.getBankName());
        paymentInfo.setIsVerified(false); // Chưa xác thực

        PaymentInfo savedPaymentInfo = paymentInfoRepository.save(paymentInfo);

        // 4. Tạo và Lưu Publisher (Liên kết Account + PaymentInfo)
        Publisher publisher = new Publisher();
        publisher.setStudioName(request.getStudioName());
        publisher.setDescription(request.getDescription());
        publisher.setWebsite(request.getWebsite());

        publisher.setAccount(savedAccount);
        publisher.setPaymentInfo(savedPaymentInfo);

        publisherRepository.save(publisher);

        // 5. Tạo Request gửi Admin
        PublisherRequest publisherRequest = new PublisherRequest();
        publisherRequest.setAccountUsername(savedAccount);
        publisherRequest.setStatus(RequestStatus.PENDING);
        publisherRequest.setCreatedAt(LocalDate.now());
        publisherRequest.setUpdatedAt(LocalDate.now());
        publisherRequestRepository.save(publisherRequest);
    }

    @Override
    public PublisherDashboardDto getPublisherDashboardStats(Long publisherId) {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        // 1. Query Doanh thu
        Double totalRevenue = orderItemRepository.sumTotalRevenueByPublisher(publisherId);
        Double monthlyRevenue = orderItemRepository.sumMonthlyRevenueByPublisher(publisherId, currentMonth, currentYear);

        // 2. Query Lượt tải (Sales)
        Long monthlyDownloads = orderItemRepository.countMonthlyDownloadsByPublisher(publisherId, currentMonth, currentYear);

        // 3. Query Đánh giá
        Double avgRating = reviewRepository.getAverageRatingByPublisher(publisherId);
        Long totalRatings = reviewRepository.countTotalRatingsByPublisher(publisherId);

        // 4. Xử lý null (nếu chưa có dữ liệu DB trả về null)
        return PublisherDashboardDto.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : 0.0)
                .monthlyRevenue(monthlyRevenue != null ? monthlyRevenue : 0.0)
                .monthlyDownloads(monthlyDownloads != null ? monthlyDownloads : 0L)
                .averageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0) // Làm tròn 1 số lẻ
                .totalRatings(totalRatings != null ? totalRatings : 0L)
                .build();
    }

    @Override
    public List<RevenueChartDto> getRevenueChart(Long publisherId, Integer year) {
        // Query database
        List<Object[]> results = orderItemRepository.getRevenueByMonthAndYear(publisherId, year);

        // Map dữ liệu: Key=Tháng, Value=Tiền
        Map<Integer, Double> revenueMap = results.stream()
                .collect(Collectors.toMap(
                        obj -> (Integer) obj[0], // Tháng
                        obj -> {
                            // SỬA LỖI TẠI ĐÂY:
                            // Kiểm tra nếu null thì trả về 0.0
                            if (obj[1] == null) return 0.0;

                            // Nếu DB trả về BigDecimal thì chuyển sang Double
                            if (obj[1] instanceof java.math.BigDecimal) {
                                return ((java.math.BigDecimal) obj[1]).doubleValue();
                            }

                            // Trường hợp khác (ví dụ DB trả về Double sẵn)
                            return (Double) obj[1];
                        }
                ));

        List<RevenueChartDto> chartData = new ArrayList<>();

        // Loop đủ 12 tháng
        for (int m = 1; m <= 12; m++) {
            Double revenue = revenueMap.getOrDefault(m, 0.0);
            chartData.add(new RevenueChartDto(m, revenue));
        }

        return chartData;
    }

    @Override
    @Transactional
    public GameDto updateGameByPublisher(Long publisherId, Long gameId, GameUpdateDto request) {
        // Tìm game
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy game với ID: " + gameId));

        GameBasicInfo info = game.getGameBasicInfos();

        // Bảo mật: Check xem game này có đúng của Publisher đang đăng nhập không
        if (!info.getPublisher().getId().equals(publisherId)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa game này!");
        }

        // Mapping dữ liệu update
        if (request.getName() != null) info.setName(request.getName());

        // Logic giá: Nếu free thì giá = 0
        if (request.getIsFree() != null && request.getIsFree()) {
            // Sửa 0.0 thành BigDecimal.ZERO
            info.setPrice(java.math.BigDecimal.ZERO);
        } else if (request.getPrice() != null) {
            // Dùng BigDecimal.valueOf để chuyển đổi từ Double
            info.setPrice(java.math.BigDecimal.valueOf(request.getPrice()));
        }

        if (request.getShortDescription() != null) info.setShortDescription(request.getShortDescription());
        if (request.getDescription() != null) info.setDescription(request.getDescription());
        if (request.getTrailerUrl() != null) info.setTrailerUrl(request.getTrailerUrl());

        // Update Category
        if (request.getCategoryId() != null) {
            Category cat = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thể loại"));
            info.setCategory(cat);
        }

        // Lưu thay đổi
        // Vì info gắn liền với game (Cascade), lưu game là lưu cả info
        Game savedGame = gameRepository.save(game);

        return gameMapper.toDTO(savedGame);
    }
}