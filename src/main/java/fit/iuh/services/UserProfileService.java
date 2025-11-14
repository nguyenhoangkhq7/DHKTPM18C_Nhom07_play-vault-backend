package fit.iuh.services;

import fit.iuh.dtos.OrderDto;
import fit.iuh.dtos.UserProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserProfileService {
    UserProfileDto getProfile(Long customerId);
    Page<OrderDto> getOrderHistory(Long customerId, Pageable pageable);
}
