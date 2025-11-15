package fit.iuh.repositories;

import fit.iuh.models.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PublisherRepository extends JpaRepository<Publisher, Long> {
}