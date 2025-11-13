package fit.iuh.controllers;

import fit.iuh.dtos.OrderDto;
import fit.iuh.dtos.UserProfileDto;
import fit.iuh.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{id}/profile")
    public UserProfileDto getProfile(@PathVariable Long id) {
        return userProfileService.getProfile(id);
    }

    @GetMapping("/{id}/orders")
    public Page<OrderDto> getOrders(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userProfileService.getOrderHistory(id, PageRequest.of(page, size));
    }
}

