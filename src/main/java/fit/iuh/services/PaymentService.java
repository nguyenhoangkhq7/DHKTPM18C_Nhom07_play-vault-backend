package fit.iuh.services;

import fit.iuh.models.Customer;
import fit.iuh.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final CustomerRepository customerRepository;

    /**
     * Thực hiện cộng tiền G-Coin vào tài khoản khách hàng.
     * @param username Tên đăng nhập của khách hàng
     * @param amount Số tiền G-Coin nạp
     * @return Customer object đã cập nhật
     */
    @Transactional
    public Customer deposit(String username, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền nạp phải lớn hơn 0.");
        }

        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng: " + username));

        // Đảm bảo balance không bị null trước khi cộng
        BigDecimal currentBalance = customer.getBalance() == null ? BigDecimal.ZERO : customer.getBalance();

        customer.setBalance(currentBalance.add(amount));

        return customerRepository.save(customer);
    }

    public Customer getCustomerByUsername(String username) {
        return customerRepository.findByAccount_Username(username)
                .orElse(null);
    }

}