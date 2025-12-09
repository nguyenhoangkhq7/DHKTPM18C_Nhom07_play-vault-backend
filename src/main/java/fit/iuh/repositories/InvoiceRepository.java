package fit.iuh.repositories;

import fit.iuh.models.Invoice;
import fit.iuh.models.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // Sử dụng EntityGraph để lấy luôn Order, OrderItems và GameBasicInfos trong 1 câu lệnh SQL
    @EntityGraph(attributePaths = {
            "order",
            "order.payment",
            "order.orderItems",
            "order.orderItems.game",
            "order.orderItems.game.gameBasicInfos"
    })
    List<Invoice> findByCustomer_Account_UsernameOrderByIssueDateDesc(String username);

    // Hàm lấy tất cả hóa đơn cho Admin (Có phân trang + Search)
    @EntityGraph(attributePaths = {"customer", "order", "order.payment"})
    @Query("SELECT i FROM Invoice i " +
            "WHERE (:status IS NULL OR i.status = :status) " + // <-- Thêm dòng này
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(i.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(i.id AS string) LIKE :keyword) " +
            "ORDER BY i.issueDate DESC")
    Page<Invoice> findAllForAdmin(
            @Param("keyword") String keyword,
            @Param("status") InvoiceStatus status, // <-- Thêm tham số này
            Pageable pageable
    );
}
