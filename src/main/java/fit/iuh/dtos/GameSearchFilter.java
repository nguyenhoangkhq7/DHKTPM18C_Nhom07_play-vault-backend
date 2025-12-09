package fit.iuh.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record GameSearchFilter(

        // --- THÔNG TIN PHẦN CỨNG ---
        @JsonPropertyDescription("Hệ điều hành máy (WINDOWS / MAC / LINUX).")
        String os,

        @JsonPropertyDescription("Tên CPU của máy (ví dụ: i5-10400F, Ryzen 5 5600G).")
        String cpu,

        @JsonPropertyDescription("Tên GPU theo model (GTX 1650, RTX 3060, RX 580…).")
        String gpu,

        @JsonPropertyDescription("Dung lượng RAM (GB). Để null nếu không rõ.")
        Integer ramGB,

        @JsonPropertyDescription("Dung lượng bộ nhớ trống (GB). Để null nếu không rõ.")
        Integer storageGB,

        // --- THÔNG TIN LỌC GAME ---
        @JsonPropertyDescription("Từ khóa tìm kiếm (áp dụng cho tên hoặc mô tả).")
        String keyword,

        @JsonPropertyDescription("Tên thể loại game (Action, RPG, Horror...).")
        String categoryName,

        @JsonPropertyDescription("Điểm đánh giá tối thiểu (0.0–5.0). Để null nếu không rõ.")
        Double minRating,

        @JsonPropertyDescription("Giá tối đa theo Gcoin. Để null nếu không rõ.")
        Double maxPrice
) {}
