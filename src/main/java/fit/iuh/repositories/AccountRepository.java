package fit.iuh.repositories;

import fit.iuh.models.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
   Optional<Account> findByUsername(String username);

   boolean existsByUsername(@NotBlank(message = "Username không được để trống") @Size(min = 4, max = 20, message = "Username phải từ 4 đến 20 ký tự") String username);

    @Query("SELECT ac FROM Account ac WHERE ac.createdAt = current_date()")  // Tinh chỉnh: uppercase SELECT, thêm () cho current_date
    List<Account> findAllAccountCreateToday();

}