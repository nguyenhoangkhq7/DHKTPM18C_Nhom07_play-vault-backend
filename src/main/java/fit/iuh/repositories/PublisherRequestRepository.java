package fit.iuh.repositories;

import fit.iuh.models.Account;
import fit.iuh.models.PublisherRequest;
import fit.iuh.models.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PublisherRequestRepository extends JpaRepository<PublisherRequest, Long> {
    Optional<PublisherRequest> findTopByAccountUsername_UsernameOrderByCreatedAtDesc(String  userName);
}
