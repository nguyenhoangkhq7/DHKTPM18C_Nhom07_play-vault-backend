package fit.iuh.services;

import fit.iuh.dtos.PublisherDto;
import org.springframework.stereotype.Service;

import java.util.List;


public interface PublisherService {
    List<PublisherDto> findAll();
}
