package fit.iuh.services.impl;

import fit.iuh.dtos.PublisherDto;
import fit.iuh.mappers.PublisherMapper;
import fit.iuh.models.Account;
import fit.iuh.models.Publisher;
import fit.iuh.repositories.PublisherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements fit.iuh.services.PublisherService {
    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;

    @Override
    public List<PublisherDto> findAll() {
        return publisherMapper.toPublisherDTOs(publisherRepository.findAll());
    }

    public PublisherDto getProfileByUsername(String username) {
        Publisher pub = publisherRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Publisher not found with username: " + username));
        return toDto(pub);
    }

    @Transactional
    public PublisherDto updateProfile(Long id, PublisherDto dto) {
        Publisher p = publisherRepository.findWithAccountById(id)
                .orElseThrow(() -> new IllegalArgumentException("Publisher not found with id: " + id));

        // cập nhật các field cho phép chỉnh
        if (dto.getName() != null)        p.setStudioName(dto.getName());
        if (dto.getDescription() != null) p.setDescription(dto.getDescription());
        if (dto.getWebsite() != null)     p.setWebsite(dto.getWebsite());

        // cập nhật trên Account
        Account acc = p.getAccount();
        if (dto.getEmail() != null) acc.setEmail(dto.getEmail());
        if (dto.getPhone() != null) acc.setPhone(dto.getPhone());

        // JPA tự flush; trả DTO mới
        return toDto(p);
    }

    private PublisherDto toDto(Publisher p) {
        Account a = p.getAccount();
        return PublisherDto.builder()
                .id(p.getId())
                .name(p.getStudioName())
                .description(p.getDescription())
                .website(p.getWebsite())
                .email(a != null ? a.getEmail() : null)
                .phone(a != null ? a.getPhone() : null)
                .username(a != null ? a.getUsername() : null)
                //.status(a != null ? a.getStatus() : null)      // nếu Account có
                .date(a != null ? a.getCreatedAt() : null)     // nếu Account có LocalDate/LocalDateTime
                .games(null) // TODO: nếu có bảng Game thì đếm thật
                .build();
    }
}
