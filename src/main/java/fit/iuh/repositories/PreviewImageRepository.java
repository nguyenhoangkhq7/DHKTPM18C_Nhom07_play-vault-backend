package fit.iuh.repositories;

import fit.iuh.models.PreviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreviewImageRepository extends JpaRepository<PreviewImage, Long> {
    List<PreviewImage> findByGameBasicInfo_Id(Long gbiId);
}
