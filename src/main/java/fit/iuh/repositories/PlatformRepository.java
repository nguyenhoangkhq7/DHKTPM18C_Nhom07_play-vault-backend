package fit.iuh.repositories;

import fit.iuh.models.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PlatformRepository extends JpaRepository<Platform, Long> {
    List<Platform> findByNameIn(Collection<String> names);
    Optional<Platform> findByName(String name);
}
