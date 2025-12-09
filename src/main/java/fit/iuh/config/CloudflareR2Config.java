// src/main/java/fit/iuh/config/CloudflareR2Config.java
package fit.iuh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "cloudflare.r2") // ← đúng
public record CloudflareR2Config(
        String accountId,
        String accessKey,
        String secretKey,
        String bucket,
        String endpoint
) {}