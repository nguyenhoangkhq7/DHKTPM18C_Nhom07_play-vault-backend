package fit.iuh.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import fit.iuh.models.enums.Os;

public record SystemSpecs(
    @JsonPropertyDescription("Hệ điều hành của máy. Ví dụ: Windows, MacOS,Linux.")
    Os os,
    @JsonPropertyDescription("Tên CPU của máy.")
    String cpu,
    @JsonPropertyDescription("Tên GPU của máy.")
    String gpu,
    @JsonPropertyDescription("Dung lượng RAM theo GB (Nếu không biết, để null).")
    Integer ramGB,
    @JsonPropertyDescription("Bộ nhớ trống theo GB (Nếu không biết, để null).")
    Integer storageGB
) {}