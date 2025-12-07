package fit.iuh.dtos;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemInfoResponse {
    private boolean success;
    private String message;
    private SystemInfoDTO systemInfo;
    private CompatibilityResult compatibilityResult;

    @Data
    @Builder
    public static class SystemInfoDTO {
        private Long id;
        private String os;
        private String cpu;
        private String gpu;
        private String ram;
        private String directxVersion;
        private LocalDateTime lastUpdated;
    }

    @Data
    @Builder
    public static class CompatibilityResult {
        private double score;
        private String level;
        private int percentage;
        private Map<String, Double> details;
        private SystemSpecs userSystem;
        private SystemSpecs requiredSystem;
        private List<String> recommendations;
    }

    @Data
    @Builder
    public static class SystemSpecs {
        private String os;
        private String cpu;
        private String gpu;
        private String ram;
    }
}