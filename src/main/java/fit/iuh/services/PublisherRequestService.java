package fit.iuh.services;

import fit.iuh.models.PublisherRequest;
import fit.iuh.models.enums.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface PublisherRequestService {
    List<PublisherRequest> getAll();

    Optional<PublisherRequest> getPublisherRequestByUserName(String userName);

    Optional<PublisherRequest> updateStatus(Long id, RequestStatus status);
}
