package fit.iuh.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record GameSearchFilter(
    // Kế thừa SystemSpecs hoặc liệt kê lại
    // Tốt nhất là liệt kê lại để kiểm soát rõ ràng
    @JsonPropertyDescription("Hệ điều hành của máy.")
    String os,
    @JsonPropertyDescription("Tên CPU của máy.")
    String cpu,
    @JsonPropertyDescription("Tên GPU của máy.")
    String gpu,
    @JsonPropertyDescription("Dung lượng RAM theo GB (Nếu không biết, để null).")
    Integer ramGB,
    @JsonPropertyDescription("Bộ nhớ trống theo GB (Nếu không biết, để null).")
    Integer storageGB,
    @JsonPropertyDescription("Từ khóa tìm kiếm (tên, mô tả).")
    String keyword,
    @JsonPropertyDescription("Tên thể loại game.")
    String categoryName,
    @JsonPropertyDescription("Điểm đánh giá tối thiểu (từ 0.0 đến 5.0, để null nếu không rõ).")
    Double minRating,
    @JsonPropertyDescription("Giá bán tối đa (theo Gcoin, để null nếu không rõ).")
    Double maxPrice
) {}