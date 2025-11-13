package fit.iuh.controllers;

import fit.iuh.dtos.GameCardDto;
import fit.iuh.dtos.GameFilterDto;
import fit.iuh.mappers.GameMapper;
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
@RequestMapping("/api/library")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private GameMapper gameMapper; // ✅ inject MapStruct mapper

    @GetMapping("/my-games")
    public ResponseEntity<List<GameCardDto>> getMyPurchasedGames(
            Authentication authentication,
            @Valid @ModelAttribute GameFilterDto filterDto
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();

        List<Game> games = libraryService.getPurchasedGames(
                username,
                filterDto.getCategoryId(),
                filterDto.getMinPrice(),
                filterDto.getMaxPrice(),
                filterDto.getStatus()
        );

        // ✅ Dùng MapStruct thay cho convertToDto
        List<GameCardDto> gameDtos = games.stream()
                .map(gameMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(gameDtos);
    }
}
