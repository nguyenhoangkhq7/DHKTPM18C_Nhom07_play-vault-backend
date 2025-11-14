package fit.iuh.services;

import fit.iuh.models.Customer;
import fit.iuh.models.Game;
import fit.iuh.models.GameSubmission;
import fit.iuh.models.enums.SubmissionStatus;
import fit.iuh.repositories.CustomerRepository;
import fit.iuh.repositories.GameSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LibraryService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GameSubmissionRepository gameSubmissionRepository;

    public List<Game> getPurchasedGames(
            String username,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String status
    ) {

        // 1. Lấy customer + thư viện game
        Customer customer = customerRepository
                .findByAccount_UsernameWithLibrary(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy customer với username: " + username
                ));

        List<Game> allOwnedGames = customer.getLibrary();

        if (allOwnedGames.isEmpty()) return List.of();

        // 2. Lấy toàn bộ submissions (TRÁNH gọi DB trong vòng lặp)
        Map<Long, GameSubmission> submissionsMap =
                gameSubmissionRepository.findAllById(
                                allOwnedGames.stream()
                                        .map(g -> g.getGameBasicInfos().getId())
                                        .collect(Collectors.toList())
                        )
                        .stream()
                        .collect(Collectors.toMap(
                                s -> s.getGameBasicInfos().getId(),
                                s -> s
                        ));

        Stream<Game> gameStream = allOwnedGames.stream();

        // === 3. Lọc Category ===
        if (categoryId != null) {
            gameStream = gameStream.filter(game ->
                    game.getGameBasicInfos() != null &&
                            game.getGameBasicInfos().getCategory() != null &&
                            game.getGameBasicInfos().getCategory().getId().equals(categoryId)
            );
        }

        // === 4. Lọc theo khoảng giá ===
        if (minPrice != null) {
            gameStream = gameStream.filter(game ->
                    game.getGameBasicInfos() != null &&
                            game.getGameBasicInfos().getPrice().compareTo(minPrice) >= 0
            );
        }

        if (maxPrice != null) {
            gameStream = gameStream.filter(game ->
                    game.getGameBasicInfos() != null &&
                            game.getGameBasicInfos().getPrice().compareTo(maxPrice) <= 0
            );
        }

        // === 5. Lọc theo trạng thái Submission ===
        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {

            SubmissionStatus filterStatus;
            try {
                filterStatus = SubmissionStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return List.of(); // status không hợp lệ → trả về empty
            }

            SubmissionStatus finalStatus = filterStatus;

            gameStream = gameStream.filter(game -> {
                Long id = game.getGameBasicInfos().getId();
                GameSubmission submission = submissionsMap.get(id);

                return submission != null &&
                        submission.getStatus() == finalStatus;
            });
        }

        return gameStream.collect(Collectors.toList());
    }
}
