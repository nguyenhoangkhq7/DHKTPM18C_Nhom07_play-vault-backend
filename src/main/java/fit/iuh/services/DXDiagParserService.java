// src/main/java/fit/iuh/services/DXDiagParserService.java
package fit.iuh.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DXDiagParserService {

    // Các section bắt buộc phải có trong file DXDiag thật
    private static final String[] REQUIRED_SECTIONS = {
            "system information",
            "dxdiag previously",
            "operating system",
            "processor",
            "memory",
            "card name",
            "display memory",
            "directx version"
    };

    public Map<String, Object> parseDXDiagFile(String fileContent) {
        Map<String, Object> systemInfo = new HashMap<>();

        try {
            System.out.println("\n=== BẮT ĐẦU KIỂM TRA FILE DXDIAG ===");

            // BƯỚC 1: KIỂM TRA FILE CÓ PHẢI DXDIAG THẬT KHÔNG?
            if (!isValidDXDiagFile(fileContent)) {
                System.err.println("File KHÔNG PHẢI DXDiag hợp lệ! Bị chặn.");
                systemInfo.put("error", "File không hợp lệ");
                systemInfo.put("os", "Unknown");
                systemInfo.put("processor", "Unknown");
                systemInfo.put("gpu", "Unknown");
                systemInfo.put("ram", 0);
                systemInfo.put("directX", "Unknown");
                return systemInfo;
            }

            System.out.println("File DXDiag HỢP LỆ! Đang phân tích...");

            // BƯỚC 2: Parse thông tin
            systemInfo.put("os", parseOSFromDXDiag(fileContent));
            systemInfo.put("processor", parseProcessorFromDXDiag(fileContent));
            systemInfo.put("gpu", parseGPUFromDXDiag(fileContent));
            systemInfo.put("ram", parseRAMFromDXDiag(fileContent));
            systemInfo.put("directX", parseDirectXFromDXDiag(fileContent));

            System.out.println("Parse thành công:");
            System.out.println("   OS: " + systemInfo.get("os"));
            System.out.println("   CPU: " + systemInfo.get("processor"));
            System.out.println("   GPU: " + systemInfo.get("gpu"));
            System.out.println("   RAM: " + systemInfo.get("ram") + " GB");
            System.out.println("   DirectX: " + systemInfo.get("directX"));
            System.out.println("=== KẾT THÚC PARSE ===\n");

        } catch (Exception e) {
            System.err.println("Lỗi khi parse DXDiag: " + e.getMessage());
            e.printStackTrace();
            systemInfo.put("error", "Lỗi xử lý file");
        }

        return systemInfo;
    }

    // HÀM QUAN TRỌNG NHẤT: KIỂM TRA FILE CÓ PHẢI DXDIAG THẬT KHÔNG
    private boolean isValidDXDiagFile(String content) {
        if (content == null || content.trim().isEmpty()) {
            System.out.println("File rỗng hoặc null");
            return false;
        }

        String lowerContent = content.toLowerCase();

        // 1. Phải có dấu hiệu của dxdiag ở đầu file
        boolean hasDxDiagHeader = lowerContent.contains("dxdiag") ||
                lowerContent.contains("directx diagnostic tool") ||
                lowerContent.contains("dxdiag previously: crashed");

        if (!hasDxDiagHeader) {
            System.out.println("Không tìm thấy dấu hiệu DXDiag ở đầu file");
            return false;
        }

        // 2. Phải có ít nhất 6/8 section quan trọng
        int foundSections = 0;
        for (String section : REQUIRED_SECTIONS) {
            if (lowerContent.contains(section)) {
                foundSections++;
            }
        }

        boolean isValid = foundSections >= 6;

        System.out.println("Tìm thấy " + foundSections + "/8 section bắt buộc → " +
                (isValid ? "HỢP LỆ" : "KHÔNG HỢP LỆ"));

        return isValid;
    }

    // ====================== PARSE CÁC THÔNG TIN ======================
    private String parseOSFromDXDiag(String content) {
        String line = findLineContaining(content, "Operating System");
        if (line == null) return "Unknown";
        int colonIndex = line.indexOf(':');
        return colonIndex != -1 ? line.substring(colonIndex + 1).trim() : "Unknown";
    }

    private String parseProcessorFromDXDiag(String content) {
        String line = findLineContaining(content, "Processor");
        if (line == null) return "Unknown";
        int colonIndex = line.indexOf(':');
        if (colonIndex == -1) return "Unknown";
        String cpu = line.substring(colonIndex + 1).trim();
        // Làm sạch tên CPU
        cpu = cpu.replaceAll("\\(R\\)", "").replaceAll("\\(TM\\)", "").replaceAll(",.*", "").trim();
        return cpu.isEmpty() ? "Unknown" : cpu;
    }

    private String parseGPUFromDXDiag(String content) {
        String gpu = findValue(content, "Card name");
        if (gpu == null || gpu.isEmpty()) {
            gpu = findValue(content, "Device Name");
        }

        // Nếu thấy Intel UHD nhưng nội dung file có đề cập NVIDIA/RTX/GTX → có khả năng có card rời
        String lowerContent = content.toLowerCase();
        if ((gpu == null || gpu.toLowerCase().contains("intel") || gpu.toLowerCase().contains("uhd"))
                && (lowerContent.contains("nvidia") || lowerContent.contains("geforce") || lowerContent.contains("rtx") || lowerContent.contains("gtx"))) {

            // Tìm tên card NVIDIA trong phần Display Devices
            Pattern nvidiaPattern = Pattern.compile("card name\\s*:(.+?)(?:\\r?\\n|$)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
            Matcher matcher = nvidiaPattern.matcher(content);
            while (matcher.find()) {
                String candidate = matcher.group(1).trim();
                if (candidate.toLowerCase().contains("nvidia") || candidate.toLowerCase().contains("geforce")) {
                    gpu = candidate;
                    break;
                }
            }
        }

        if (gpu == null || gpu.trim().isEmpty()) {
            gpu = "Unknown";
        } else {
            gpu = gpu.replace("NVIDIA ", "").replace("GeForce ", "").replace("AMD ", "").trim();
            if (lowerContent.contains("laptop") || gpu.toLowerCase().contains("mobile")) {
                gpu += " (Laptop)";
            }
        }

        return gpu;
    }

    private Integer parseRAMFromDXDiag(String content) {
        String line = findLineContaining(content, "Memory");
        if (line == null) return 0;

        Matcher matcher = Pattern.compile("(\\d+)\\s*MB").matcher(line);
        if (matcher.find()) {
            int mb = Integer.parseInt(matcher.group(1));
            return Math.round(mb / 1024.0f);
        }
        return 0;
    }

    private String parseDirectXFromDXDiag(String content) {
        String line = findLineContaining(content, "DirectX Version");
        if (line == null) return "Unknown";
        int colonIndex = line.indexOf(':');
        return colonIndex != -1 ? line.substring(colonIndex + 1).trim() : "Unknown";
    }

    // ====================== HELPER METHODS ======================
    private String findLineContaining(String content, String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        for (String line : content.split("\n")) {
            if (line.toLowerCase().contains(lowerKeyword)) {
                return line;
            }
        }
        return null;
    }

    private String findValue(String content, String keyword) {
        String line = findLineContaining(content, keyword);
        if (line == null) return null;
        int colonIndex = line.indexOf(':');
        return colonIndex != -1 ? line.substring(colonIndex + 1).trim() : null;
    }
}