package fit.iuh.repositories;

import fit.iuh.dtos.InvoiceTableDto;
import fit.iuh.models.Invoice;
import fit.iuh.models.enums.InvoiceStatus;
import fit.iuh.models.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Query tối ưu: Lấy trực tiếp DTO, lọc Payment SUCCESS ngay tại nguồn
    @Query("SELECT new fit.iuh.dtos.InvoiceTableDto(" +
            "  i.id, " +
            "  c.fullName, " +
            "  a.email, " +
            "  i.issueDate, " +
            "  i.totalAmount, " +
            "  i.status, " +
            "  p.paymentMethod " + // Lấy phương thức của giao dịch thành công (nếu có)
            ") " +
            "FROM Invoice i " +
            "JOIN i.customer c " +
            "JOIN c.account a " +
            // Quan trọng: Chỉ join với payment nào có status = SUCCESS
            // Nếu không có success payment, p sẽ là null (nhờ LEFT JOIN), hóa đơn vẫn hiện ra
            "LEFT JOIN Payment p ON p.invoice.id = i.id AND p.status = fit.iuh.models.enums.PaymentStatus.SUCCESS " +
            "WHERE (:status IS NULL OR i.status = :status) " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(i.id AS string) LIKE :keyword) " +
            "ORDER BY i.issueDate DESC")
    Page<InvoiceTableDto> findAllForAdminCustom(
            @Param("keyword") String keyword,
            @Param("status") InvoiceStatus status,
            Pageable pageable
    );
}