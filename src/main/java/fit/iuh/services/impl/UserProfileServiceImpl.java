package fit.iuh.services.impl;

import fit.iuh.dtos.OrderDto;
import fit.iuh.dtos.UserProfileDto;
import fit.iuh.mappers.OrderMapper;
import fit.iuh.mappers.UserProfileMapper;
import fit.iuh.models.Customer;
import fit.iuh.models.Order;
import fit.iuh.repositories.CustomerRepository;
import fit.iuh.repositories.OrderRepository;
import fit.iuh.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final UserProfileMapper userProfileMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getProfile(Long customerId) {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        return userProfileMapper.toDto(c);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrderHistory(Long customerId, Pageable pageable) {
        Page<Order> page = orderRepository.findByCustomer_Id(customerId, pageable);

        List<OrderDto> dtoList = page.getContent().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }
}
