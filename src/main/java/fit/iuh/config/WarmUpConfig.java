// File: src/main/java/fit/iuh/config/WarmUpConfig.java
package fit.iuh.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WarmUpConfig {

    @Bean
    ApplicationRunner warmUpOllama(ChatClient chatClient) {
        return args -> {
            System.out.println("🚀 Đang khởi động Model Chat (Warm-up), vui lòng chờ...");
            long startTime = System.currentTimeMillis();
            
            try {
                // Gửi một request đơn giản để buộc Ollama load model vào RAM
                chatClient.prompt().user("Tên bạn là gì?").call().content();
                long endTime = System.currentTimeMillis();

                System.out.println("✅ Model đã sẵn sàng phục vụ! Warm-up mất: " + (endTime - startTime) + "ms");
            } catch (Exception e) {
                // Nếu thất bại (timeout/error), ít nhất model cũng đã cố gắng load
                System.err.println("⚠️ Warm-up thất bại (model có thể chưa load hoàn toàn): " + e.getMessage());
            }
        };
    }
}