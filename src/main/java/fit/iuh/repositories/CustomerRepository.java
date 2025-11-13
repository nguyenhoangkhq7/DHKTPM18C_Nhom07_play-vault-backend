package fit.iuh.repositories;

import fit.iuh.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Tìm Customer bằng username VÀ lập tức tải (FETCH) toàn bộ
     * thư viện game (cùng với GameBasicInfos) của họ.
     *
     * "LEFT JOIN FETCH c.library g" -> Tải tất cả Game trong thư viện
     * "LEFT JOIN FETCH g.gameBasicInfos" -> Tải GameBasicInfo của mỗi Game
     *
     * Điều này ngăn chặn lỗi N+1 query.
     */
    @Query("SELECT c FROM Customer c " +
            "LEFT JOIN FETCH c.library g " +
            "LEFT JOIN FETCH g.gameBasicInfos " +
            "WHERE c.account.username = :username")
    Optional<Customer> findByAccount_UsernameWithLibrary(
            @Param("username") String username
    );

    // Bạn vẫn có thể giữ lại hàm cũ nếu dùng ở nơi khác
    Optional<Customer> findByAccount_Username(String username);
}