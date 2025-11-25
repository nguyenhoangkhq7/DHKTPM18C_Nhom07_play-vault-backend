package fit.iuh.services.impl;

import fit.iuh.models.PublisherRequest;
import fit.iuh.models.enums.RequestStatus;
import fit.iuh.repositories.PublisherRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PublisherRequestServiceImpl implements fit.iuh.services.PublisherRequestService {
    private final PublisherRequestRepository repository;

    @Override
    public List<PublisherRequest> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<PublisherRequest> getPublisherRequestByUserName(String userName) {
        return repository.findTopByAccountUsername_UsernameOrderByCreatedAtDesc(userName);
    }

    @Override
    public Optional<PublisherRequest> updateStatus(Long id, RequestStatus status){
        return repository.findById(id).map(req -> {
            req.setStatus(status);
            req.setUpdatedAt(LocalDate.now());
            return repository.save(req); // tự động update DB
        });
    }


}
