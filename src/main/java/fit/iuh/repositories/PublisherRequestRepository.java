package fit.iuh.repositories;

import fit.iuh.models.Account;
import fit.iuh.models.PublisherRequest;
import fit.iuh.models.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublisherRequestRepository extends JpaRepository<PublisherRequest, Long> {
    List<PublisherRequest> findByStatus(RequestStatus status);
    Optional<PublisherRequest> findTopByAccountUsername_UsernameOrderByCreatedAtDesc(String userName);
}