package fit.iuh.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record SystemSpecs(

        @JsonPropertyDescription("""
                Hệ điều hành máy (AI có thể nhập tự do).
                Ví dụ hợp lệ: Windows, Win 11, macOS, OSX, Linux, Ubuntu...
                """)
        String os,

        @JsonPropertyDescription("Tên CPU của máy (ví dụ: Intel i5-10400, Ryzen 5 3600).")
        String cpu,

        @JsonPropertyDescription("Tên GPU của máy (ví dụ: GTX 1060, RTX 3060, RX 580).")
        String gpu,

        @JsonPropertyDescription("""
                Dung lượng RAM (GB). Nếu không rõ, để null.
                """)
        Integer ramGB,

        @JsonPropertyDescription("""
                Bộ nhớ trống (GB). Nếu không rõ, để null.
                """)
        Integer storageGB
) {}
