package fit.iuh.services.impl;

import fit.iuh.dtos.OrderDto;
import fit.iuh.dtos.OrderItemDto;
import fit.iuh.dtos.UserProfileDto;
import fit.iuh.models.*;
import fit.iuh.repositories.CustomerRepository;
import fit.iuh.repositories.OrderRepository;
import fit.iuh.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getProfile(Long customerId) {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        UserProfileDto dto = new UserProfileDto();
        dto.setId(c.getId());
        dto.setFullName(c.getFullName());
        dto.setDateOfBirth(c.getDateOfBirth());
        dto.setBalance(c.getBalance());

        if (c.getAccount() != null) {
            dto.setUsername(c.getAccount().getUsername());
            dto.setEmail(c.getAccount().getEmail());
            dto.setPhone(c.getAccount().getPhone());
        }

        // Lấy address, avatarUrl từ Customer (yêu cầu Customer có các field này)
        dto.setAddress(c.getAddress());
        dto.setAvatarUrl(c.getAvatarUrl());

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrderHistory(Long customerId, Pageable pageable) {
        Page<Order> page = orderRepository.findByCustomer_Id(customerId, pageable);

        var dtoList = page.getContent().stream()
                .map(this::toOrderDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    private OrderDto toOrderDto(Order o) {
        OrderDto dto = new OrderDto();
        dto.setId(o.getId());
        dto.setCreatedAt(o.getCreatedAt());
        dto.setTotal(o.getTotal());
        dto.setStatus(o.getStatus());

        if (o.getItems() != null) {
            dto.setItems(o.getItems().stream().map(this::toOrderItemDto).collect(Collectors.toList()));
        } else {
            dto.setItems(Collections.emptyList());
        }

        return dto;
    }

    private OrderItemDto toOrderItemDto(OrderItem item) {
        OrderItemDto idto = new OrderItemDto();
        idto.setId(item.getId());
        idto.setPrice(item.getPrice());
        idto.setTotal(item.getTotal());

        if (item.getGame() != null && item.getGame().getGameBasicInfos() != null) {
            var basic = item.getGame().getGameBasicInfos();
            idto.setGameId(basic.getId());
            idto.setGameTitle(basic.getName());
            idto.setGameThumbnail(basic.getThumbnail());
        } else if (item.getGame() != null) {
            idto.setGameId(item.getGame().getId());
        }

        return idto;
    }
}
