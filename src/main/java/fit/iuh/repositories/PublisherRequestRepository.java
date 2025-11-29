package fit.iuh.repositories;

import fit.iuh.models.PublisherRequest;
import fit.iuh.models.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublisherRequestRepository extends JpaRepository<PublisherRequest, Long> {
    // Hàm này để tìm các request theo trạng thái (VD: tìm list PENDING để Admin duyệt)
    List<PublisherRequest> findByStatus(RequestStatus status);
}