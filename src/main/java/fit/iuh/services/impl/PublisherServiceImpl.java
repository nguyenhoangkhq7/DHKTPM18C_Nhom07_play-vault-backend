package fit.iuh.services.impl;

import fit.iuh.dtos.PublisherDto;
import fit.iuh.mappers.PublisherMapper;
import fit.iuh.repositories.PublisherRepository;
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
}
