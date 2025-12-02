package fit.iuh.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendBlockEmail(String toEmail, String username, String reason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Tài khoản của bạn đã bị khóa tạm thời - PlayVault");
//            helper.setFrom("no-reply@playvault@gmail.com");

            String html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                    <h2 style="color: #d32f2f;">Tài khoản bị khóa</h2>
                    <p>Xin chào <strong>%s</strong>,</p>
                    <p>Chúng tôi rất tiếc phải thông báo tài khoản của bạn đã bị <strong>khóa tạm thời</strong>.</p>
                    <p><strong>Lý do:</strong> %s</p>
                    <p>Nếu bạn cho rằng đây là nhầm lẫn, vui lòng liên hệ hỗ trợ ngay:</p>
                    <p>Email: support@playvault.com</p>
                    <br>
                    <p>Trân trọng,<br>Đội ngũ PlayVault</p>
                </div>
                """.formatted(username, reason);

            helper.setText(html, true);
            mailSender.send(message);

            log.info("Đã gửi email khóa tài khoản đến: {}", toEmail);
        } catch (Exception e) {
            log.error("Gửi email thất bại cho {}: {}", toEmail, e.getMessage());
            // Không throw exception → không làm crash API block
        }
    }

    @Async
    public void sendPublisherApprovedEmail(String toEmail, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Chúc mừng! Yêu cầu làm Publisher của bạn đã được duyệt");

            String html = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #4CAF50; border-radius: 10px; background: #f9fff9;">
                <h2 style="color: #2e7d32;">Chúc mừng %s!</h2>
                <p>Yêu cầu trở thành <strong>Publisher</strong> của bạn đã được <strong>DUYỆT</strong>!</p>
                <p>Bây giờ bạn có thể đăng tải nội dung và kiếm tiền trên PlayVault rồi!</p>
                <p>Chúc bạn thành công rực rỡ!</p>
                <br>
                <p>Trân trọng,<br>Đội ngũ PlayVault</p>
            </div>
            """.formatted(username);

            helper.setText(html, true);
            mailSender.send(message);
            log.info("Đã gửi email duyệt Publisher đến: {}", toEmail);
        } catch (Exception e) {
            log.error("Gửi email duyệt Publisher thất bại: {}", e.getMessage());
        }
    }

    @Async
    public void sendPublisherRejectedEmail(String toEmail, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Thông báo: Yêu cầu làm Publisher bị từ chối");

            String html = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #d32f2f; border-radius: 10px; background: #fff5f5;">
                <h2 style="color: #c62828;">Xin lỗi %s,</h2>
                <p>Yêu cầu trở thành <strong>Publisher</strong> của bạn <strong>không được chấp nhận</strong> trong lần này.</p>
                <p>Bạn vui lòng kiểm tra lại hồ sơ và gửi yêu cầu mới khi đã đáp ứng đủ điều kiện nhé!</p>
                <p>Nếu có thắc mắc, hãy liên hệ đội ngũ hỗ trợ của chúng tôi.</p>
                <br>
                <p>Trân trọng,<br>Đội ngũ PlayVault</p>
            </div>
            """.formatted(username);

            helper.setText(html, true);
            mailSender.send(message);

            log.info("Đã gửi email từ chối Publisher đến: {}", toEmail);
        } catch (Exception e) {
            log.error("Gửi email từ chối Publisher thất bại cho {}: {}", toEmail, e.getMessage());
        }
    }


    /**
     * Gửi email thông báo kết quả xử lý báo cáo sự cố
     */
    @Async
    public void sendReportResultEmail(String toEmail, String username, Long reportId, String reportTitle, boolean isApproved, String adminNote) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String subject = isApproved ? "✅ Khiếu nại của bạn đã được giải quyết - PlayVault"
                    : "❌ Thông báo về khiếu nại của bạn - PlayVault";

            helper.setTo(toEmail);
            helper.setSubject(subject);

            // Tùy chỉnh màu sắc và nội dung dựa trên trạng thái duyệt
            String headerColor = isApproved ? "#2e7d32" : "#c62828"; // Xanh hoặc Đỏ
            String statusText = isApproved ? "ĐÃ ĐƯỢC CHẤP NHẬN" : "ĐÃ BỊ TỪ CHỐI";
            String introText = isApproved
                    ? "Chúng tôi đã xác minh và xử lý sự cố cho đơn hàng của bạn."
                    : "Sau khi kiểm tra, chúng tôi không thể xác thực yêu cầu của bạn.";

            String html = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                <h2 style="color: %s;">Kết quả xử lý báo cáo #%d</h2>
                <p>Xin chào <strong>%s</strong>,</p>
                <p>Chúng tôi gửi mail này để thông báo về báo cáo: <strong>"%s"</strong>.</p>
                
                <div style="background-color: #f5f5f5; padding: 15px; border-radius: 5px; margin: 15px 0;">
                    <p><strong>Trạng thái:</strong> <span style="color: %s; font-weight: bold;">%s</span></p>
                    <p>%s</p>
                    <p><strong>Ghi chú từ Admin:</strong></p>
                    <p style="font-style: italic; color: #555;">"%s"</p>
                </div>

                <p>Cảm ơn bạn đã sử dụng dịch vụ của PlayVault.</p>
                <br>
                <p>Trân trọng,<br>Đội ngũ Hỗ trợ PlayVault</p>
            </div>
            """.formatted(headerColor, reportId, username, reportTitle, headerColor, statusText, introText, adminNote);

            helper.setText(html, true);
            mailSender.send(message);

            log.info("Đã gửi email kết quả báo cáo #{} đến: {}", reportId, toEmail);
        } catch (Exception e) {
            log.error("Gửi email báo cáo thất bại cho {}: {}", toEmail, e.getMessage());
        }
    }
}
