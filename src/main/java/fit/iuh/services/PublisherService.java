package fit.iuh.services;

import fit.iuh.dtos.PublisherDto;
import fit.iuh.models.Publisher;
import org.springframework.stereotype.Service;

import java.util.List;


public interface PublisherService {
    List<PublisherDto> findAll();
    PublisherDto updateProfile(Long id, PublisherDto dto);
    public PublisherDto getProfileByUsername(String username);
}
