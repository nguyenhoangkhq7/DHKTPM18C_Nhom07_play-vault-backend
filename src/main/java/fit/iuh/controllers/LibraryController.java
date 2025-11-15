package fit.iuh.controllers;

import fit.iuh.dtos.GameCardDto;
import fit.iuh.dtos.GameFilterDto;
import fit.iuh.mappers.GameMapper; // ✅ Import đã có
import fit.iuh.models.Game;
import fit.iuh.services.LibraryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/library") // Giữ nguyên theo file của bạn
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    // --- BƯỚC 1.1: BỎ COMMENT DÒNG NÀY ---
    @Autowired
    private GameMapper gameMapper; // ✅ Kích hoạt MapStruct

    @GetMapping("/my-games")
    public ResponseEntity<List<GameCardDto>> getMyPurchasedGames(
            Authentication authentication,
            @Valid @ModelAttribute GameFilterDto filterDto
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();

        List<Game> games = libraryService.getPurchasedGames(username, filterDto);

        // --- BƯỚC 1.2: DÙNG gameMapper THAY VÌ convertToDto ---
        List<GameCardDto> gameDtos = games.stream()
                .map(gameMapper::toCardDto) // ✅ Thay thế ở đây
                .collect(Collectors.toList());
        // --- KẾT THÚC THAY ĐỔI ---

        return ResponseEntity.ok(gameDtos);
    }


}