package fit.iuh.services;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConfigComparisonService {

    // ====================== BENCHMARK DATA CHO FPS ESTIMATION ======================
    private static final Map<String, Map<String, Double>> GAME_FPS_BENCHMARKS = new HashMap<>();
    static {
        // Cyberpunk 2077 - Base FPS với RTX 3070 Laptop + i7-11800H
        Map<String, Double> cyberpunk = new HashMap<>();
        cyberpunk.put("1080p_low", 130.0);
        cyberpunk.put("1080p_medium", 95.0);
        cyberpunk.put("1080p_high", 65.0);
        cyberpunk.put("1080p_ultra", 45.0);
        cyberpunk.put("1440p_high", 50.0);
        GAME_FPS_BENCHMARKS.put("cyberpunk-2077", cyberpunk);
        GAME_FPS_BENCHMARKS.put("cyberpunk 2077", cyberpunk);

        // Elden Ring - Base FPS với RTX 3070 Laptop + i7-11800H
        Map<String, Double> elden = new HashMap<>();
        elden.put("1080p_low", 120.0);
        elden.put("1080p_medium", 100.0);
        elden.put("1080p_high", 75.0);
        elden.put("1080p_ultra", 65.0);
        elden.put("1440p_high", 55.0);
        GAME_FPS_BENCHMARKS.put("elden-ring", elden);
        GAME_FPS_BENCHMARKS.put("elden ring", elden);

        // GTA V
        Map<String, Double> gta = new HashMap<>();
        gta.put("1080p_low", 200.0);
        gta.put("1080p_medium", 160.0);
        gta.put("1080p_high", 120.0);
        gta.put("1080p_ultra", 90.0);
        GAME_FPS_BENCHMARKS.put("gta-v", gta);
        GAME_FPS_BENCHMARKS.put("gta 5", gta);

        // Valorant (rất nhẹ)
        Map<String, Double> valorant = new HashMap<>();
        valorant.put("1080p_low", 400.0);
        valorant.put("1080p_medium", 350.0);
        valorant.put("1080p_high", 300.0);
        GAME_FPS_BENCHMARKS.put("valorant", valorant);
    }

    public Map<String, Object> compareConfiguration(
            Map<String, Object> userSystem,
            Map<String, Object> gameRequirements) {

        Map<String, Object> result = new HashMap<>();

        try {
            Integer userRam = parseRamValue((String) userSystem.getOrDefault("ram", "0"));
            String userGpu = ((String) userSystem.getOrDefault("gpu", "Unknown")).trim();
            String userCpu = ((String) userSystem.getOrDefault("cpu", "Unknown")).trim();
            String userOs = ((String) userSystem.getOrDefault("os", "Unknown")).trim();

            String gameRamStr = convertToString(gameRequirements.getOrDefault("ram", "8"));
            Integer gameRam = parseRamValue(gameRamStr);
            String gameGpu = convertToString(gameRequirements.getOrDefault("gpu", "2GB")).trim();
            String gameCpu = convertToString(gameRequirements.getOrDefault("cpu", "Core i5")).trim();
            String gameOs = convertToString(gameRequirements.getOrDefault("os", "Windows 10")).trim();
            String gameName = convertToString(gameRequirements.getOrDefault("name", "unknown")).toLowerCase();

            double ramScore = evaluateRAM(userRam, gameRam);
            double gpuScore = evaluateGPU(userGpu, gameGpu);
            double cpuScore = evaluateCPU(userCpu, gameCpu);
            double osScore = evaluateOS(userOs, gameOs);

            double totalScore = (ramScore * 0.25) + (gpuScore * 0.40) + (cpuScore * 0.25) + (osScore * 0.10);

            String compatibilityLevel = getCompatibilityLevel(totalScore);

            // TÍCH HỢP FPS ESTIMATION
            Map<String, Double> fpsEstimate = estimateFPS(gameName, totalScore);

            // TÍCH HỢP BENCHMARK SCORE (% so với high-end)
            double benchmarkScore = calculateBenchmarkScore(gpuScore, cpuScore);

            List<String> recommendations = generateRecommendations(
                    ramScore, gpuScore, cpuScore, osScore,
                    userRam, gameRam, userGpu, gameGpu, userCpu, gameCpu, userOs, gameOs);

            result.put("success", true);
            result.put("score", Math.round(totalScore * 100.0) / 100.0);
            result.put("level", compatibilityLevel);
            result.put("percentage", (int) (totalScore * 100));
            result.put("fpsEstimate", fpsEstimate);
            result.put("benchmarkScore", Math.round(benchmarkScore));
            result.put("recommendations", recommendations);

            Map<String, Double> details = new HashMap<>();
            details.put("ram", Math.round(ramScore * 100.0) / 100.0);
            details.put("gpu", Math.round(gpuScore * 100.0) / 100.0);
            details.put("cpu", Math.round(cpuScore * 100.0) / 100.0);
            details.put("os", Math.round(osScore * 100.0) / 100.0);
            result.put("details", details);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("ram", userRam + " GB");
            userInfo.put("gpu", userGpu);
            userInfo.put("cpu", userCpu);
            userInfo.put("os", userOs);
            result.put("userSystem", userInfo);

            Map<String, Object> requiredInfo = new HashMap<>();
            requiredInfo.put("ram", gameRam + " GB");
            requiredInfo.put("gpu", gameGpu);
            requiredInfo.put("cpu", gameCpu);
            requiredInfo.put("os", gameOs);
            result.put("requiredSystem", requiredInfo);

        } catch (Exception e) {
            System.err.println("Error in compareConfiguration: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("score", 0);
            result.put("level", "Không xác định");
            result.put("error", "Lỗi khi so sánh cấu hình");
        }

        return result;
    }

    // ====================== FPS ESTIMATION ======================
    private Map<String, Double> estimateFPS(String gameName, double totalScore) {
        Map<String, Double> fpsMap = new HashMap<>();

        for (Map.Entry<String, Map<String, Double>> entry : GAME_FPS_BENCHMARKS.entrySet()) {
            if (gameName.contains(entry.getKey().replace("-", " ").replace("  ", " "))) {
                for (Map.Entry<String, Double> preset : entry.getValue().entrySet()) {
                    double estimated = preset.getValue() * totalScore;
                    estimated = Math.max(10.0, Math.round(estimated / 5.0) * 5.0); // Làm tròn 5 FPS
                    fpsMap.put(preset.getKey(), estimated);
                }
                return fpsMap;
            }
        }

        // Fallback nếu game chưa có data
        double base = 60.0 * totalScore;
        fpsMap.put("1080p_medium", Math.round(base / 5.0) * 5.0);
        fpsMap.put("1080p_high", Math.round(base * 0.8 / 5.0) * 5.0);
        return fpsMap;
    }

    // ====================== BENCHMARK SCORE ======================
    private double calculateBenchmarkScore(double gpuScore, double cpuScore) {
        // So sánh với cấu hình "high-end trung bình" (RTX 3070 + i7-12700K)
        double gpuWeight = gpuScore * 1.3;
        double cpuWeight = cpuScore * 1.1;
        double score = (gpuWeight * 0.65 + cpuWeight * 0.35) * 100;
        return Math.min(200.0, score); // Cap ở 200%
    }

    // ====================== RAM ======================
    private double evaluateRAM(Integer userRam, Integer gameRam) {
        if (gameRam == null || gameRam == 0) return 1.0;
        double ratio = (double) userRam / gameRam;
        if (ratio >= 1.0) return 1.0;
        if (ratio >= 0.8) return 0.85;
        if (ratio >= 0.6) return 0.6;
        if (ratio >= 0.4) return 0.3;
        return 0.1;
    }

    // ====================== GPU ======================
    private double evaluateGPU(String userGpu, String gameGpu) {
        if (gameGpu == null || gameGpu.isEmpty() || gameGpu.equalsIgnoreCase("any") ||
                gameGpu.toLowerCase().contains("integrated") || gameGpu.toLowerCase().contains("uhd")) {
            return 1.0;
        }

        String user = userGpu.toLowerCase().trim();
        String game = gameGpu.toLowerCase().trim();

        String[] highEnd = {"rtx 4090", "rtx 4080", "rtx 4070", "rtx 3090", "rtx 3080", "rtx 3070", "rtx 4060", "rtx 4050"};
        String[] upperMid = {"rtx 3060", "rtx 3050", "rtx 2080", "rtx 2070", "gtx 1080 ti", "gtx 1080"};
        String[] midRange = {"gtx 1070", "gtx 1660 ti", "gtx 1660 super", "rtx 2060", "gtx 1070 ti"};
        String[] entryLevel = {"gtx 1060", "gtx 1650", "gtx 1050 ti", "rtx 3050 laptop", "gtx 1050"};
        String[] lowEnd = {"gt 1030", "gtx 750 ti", "integrated", "intel uhd", "intel iris", "amd radeon"};

        double gameLevel = getGpuLevel(game, highEnd, 4.0, upperMid, 3.5, midRange, 3.0, entryLevel, 2.5, lowEnd, 1.5);
        if (game.contains("2gb") || game.contains("dedicated")) gameLevel = Math.max(gameLevel, 2.0);

        double userLevel = getGpuLevel(user, highEnd, 5.0, upperMid, 4.5, midRange, 4.0, entryLevel, 3.5, lowEnd, 1.0);
        if (user.contains("rtx")) userLevel = Math.max(userLevel, 4.0);
        if (user.contains("gtx 10") && !user.contains("1050")) userLevel = Math.max(userLevel, 3.8);
        if (user.contains("1050")) userLevel = 2.5;
        if (user.contains("integrated") || user.contains("uhd") || user.contains("iris")) userLevel = 1.0;

        double ratio = userLevel / gameLevel;
        if (ratio >= 1.5) return 1.0;
        if (ratio >= 1.2) return 0.95;
        if (ratio >= 1.0) return 0.9;
        if (ratio >= 0.8) return 0.7;
        if (ratio >= 0.6) return 0.5;
        return 0.3;
    }

    private double getGpuLevel(String text, String[] t1, double v1, String[] t2, double v2,
                               String[] t3, double v3, String[] t4, double v4, String[] t5, double v5) {
        if (containsAny(text, t1)) return v1;
        if (containsAny(text, t2)) return v2;
        if (containsAny(text, t3)) return v3;
        if (containsAny(text, t4)) return v4;
        if (containsAny(text, t5)) return v5;
        return 3.0;
    }

    private boolean containsAny(String text, String[] keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) return true;
        }
        return false;
    }

    // ====================== CPU (INTEL & AMD) ======================
    private double evaluateCPU(String userCpu, String gameCpu) {
        if (gameCpu == null || gameCpu.isEmpty()) return 1.0;

        String user = userCpu.toLowerCase().trim();
        String game = gameCpu.toLowerCase().trim();

        if (game.contains("core i3") || game.contains("ryzen 3") || game.contains("pentium") || game.contains("celeron")) {
            return user.contains("i5") || user.contains("i7") || user.contains("i9") || user.contains("ryzen") ? 1.0 : 0.7;
        }
        if (game.contains("core i5") || game.contains("ryzen 5")) {
            return user.contains("i7") || user.contains("i9") || user.contains("ryzen 7") || user.contains("ryzen 9") ? 1.0 : 0.9;
        }
        if (game.contains("core i7") || game.contains("ryzen 7")) {
            return user.contains("i9") || user.contains("ryzen 9") ? 1.0 : 0.9;
        }

        if (isIntel(user) && isIntel(game)) {
            int userGen = extractIntelGen(user);
            int gameGen = extractIntelGen(game);
            if (userGen == gameGen && userGen >= 8) return compareSameGenIntel(user, game);
            if (userGen > gameGen + 1) return 1.0;
            if (userGen > gameGen) return 0.95;
            if (userGen == gameGen) return 0.85;
            if (userGen == gameGen - 1) return 0.6;
            return 0.4;
        }

        if (isAmd(user) && isAmd(game)) {
            int userSeries = extractAmdSeries(user);
            int gameSeries = extractAmdSeries(game);
            if (userSeries > gameSeries + 1) return 1.0;
            if (userSeries > gameSeries) return 0.95;
            if (userSeries == gameSeries) return compareSameSeriesAmd(user, game);
            if (userSeries == gameSeries - 1) return 0.6;
            return 0.4;
        }

        if (isAmd(user) && isIntel(game)) {
            int userSeries = extractAmdSeries(user);
            int gameGen = extractIntelGen(game);
            int equiv = userSeries >= 7000 ? gameGen + 2 : userSeries >= 5000 ? gameGen + 1 : gameGen;
            if (equiv > gameGen + 1) return 1.0;
            if (equiv > gameGen) return 0.95;
            return 0.8;
        }

        if (isIntel(user) && isAmd(game)) {
            int userGen = extractIntelGen(user);
            int gameSeries = extractAmdSeries(game);
            int equiv = gameSeries >= 7000 ? userGen + 2 : gameSeries >= 5000 ? userGen + 1 : userGen;
            if (userGen >= equiv) return 0.9;
            return 0.6;
        }

        if (user.contains("ryzen") || user.contains("13") || user.contains("14") || user.contains("7000") || user.contains("9000")) {
            return 0.95;
        }
        return 0.7;
    }

    private boolean isIntel(String cpu) { return cpu.contains("intel") || cpu.contains("core") || cpu.contains("i3") || cpu.contains("i5") || cpu.contains("i7") || cpu.contains("i9"); }
    private boolean isAmd(String cpu) { return cpu.contains("amd") || cpu.contains("ryzen"); }

    private int extractIntelGen(String cpu) {
        if (cpu.contains("14")) return 14;
        if (cpu.contains("13")) return 13;
        if (cpu.contains("12")) return 12;
        if (cpu.contains("11")) return 11;
        if (cpu.contains("10")) return 10;
        if (cpu.contains("9")) return 9;
        if (cpu.contains("8")) return 8;
        if (cpu.contains("7")) return 7;
        return 6;
    }

    private int extractAmdSeries(String cpu) {
        if (cpu.contains("9000")) return 9000;
        if (cpu.contains("8000")) return 8000;
        if (cpu.contains("7000")) return 7000;
        if (cpu.contains("5000")) return 5000;
        if (cpu.contains("3000")) return 3000;
        return 1000;
    }

    private double compareSameGenIntel(String user, String game) {
        String[] ranks = {"k", "hq", "h", ""};
        int userRank = getRank(user, ranks);
        int gameRank = getRank(game, ranks);
        if (userRank < gameRank) return 1.0;
        if (userRank == gameRank) return 0.9;
        if (userRank == gameRank + 1) return 0.7;
        return 0.5;
    }

    private double compareSameSeriesAmd(String user, String game) {
        int userLevel = user.contains("ryzen 9") ? 4 : user.contains("ryzen 7") ? 3 : user.contains("ryzen 5") ? 2 : 1;
        int gameLevel = game.contains("ryzen 9") ? 4 : game.contains("ryzen 7") ? 3 : game.contains("ryzen 5") ? 2 : 1;
        if (userLevel > gameLevel) return 1.0;
        if (userLevel == gameLevel) return 0.9;
        return 0.7;
    }

    private int getRank(String cpu, String[] ranks) {
        for (int i = 0; i < ranks.length; i++) {
            if (cpu.contains(ranks[i])) return i;
        }
        return ranks.length - 1;
    }

    // ====================== OS ======================
    private double evaluateOS(String userOs, String gameOs) {
        if (gameOs == null || gameOs.isEmpty()) return 1.0;
        String user = userOs.toLowerCase().trim();
        String game = gameOs.toLowerCase().trim();
        if (user.equals(game)) return 1.0;
        if (game.contains("windows")) {
            if (user.contains("windows 11") || user.contains("windows 10")) return 1.0;
            if (user.contains("windows 7") && game.contains("windows 7")) return 1.0;
            if (user.contains("windows 7")) return 0.3;
        }
        if (game.contains("mac") && user.contains("mac")) return 1.0;
        if (game.contains("linux") && user.contains("linux")) return 1.0;
        return game.contains("windows") ? 0.7 : 0.3;
    }

    // ====================== LEVEL & RECOMMENDATIONS ======================
    private String getCompatibilityLevel(double score) {
        if (score >= 0.9) return "Rất cao";
        if (score >= 0.7) return "Cao";
        if (score >= 0.5) return "Trung bình";
        if (score >= 0.3) return "Thấp";
        return "Rất thấp";
    }

    private List<String> generateRecommendations(
            double ramScore, double gpuScore, double cpuScore, double osScore,
            Integer userRam, Integer gameRam,
            String userGpu, String gameGpu,
            String userCpu, String gameCpu,
            String userOs, String gameOs) {

        List<String> recommendations = new ArrayList<>();

        if (ramScore < 0.7) {
            int needed = gameRam - userRam;
            if (needed > 0) recommendations.add("RAM không đủ. Bạn cần thêm ít nhất " + needed + "GB RAM.");
        } else if (ramScore < 0.9) {
            recommendations.add("RAM vừa đủ. Nên nâng cấp để chơi tốt hơn.");
        }

        if (gpuScore < 0.7) recommendations.add("Card đồ họa yếu. Cân nhắc nâng cấp để chơi mượt.");
        else if (gpuScore < 0.9) recommendations.add("Card đồ họa vừa phải. Có thể chơi ở cấu hình thấp.");

        if (cpuScore < 0.7) recommendations.add("CPU không đủ mạnh. Nên nâng cấp processor.");
        else if (cpuScore < 0.9) recommendations.add("CPU vừa đủ. Game sẽ chạy ở tốc độ chấp nhận được.");

        if (osScore < 1.0) recommendations.add("Hệ điều hành của bạn có thể không tương thích hoàn toàn.");

        if (ramScore >= 0.9 && gpuScore >= 0.9 && cpuScore >= 0.9 && osScore >= 0.9) {
            recommendations.add("Cấu hình của bạn rất tốt để chơi game này!");
        }

        if (recommendations.isEmpty()) recommendations.add("Cấu hình của bạn phù hợp để chơi game này.");

        return recommendations;
    }

    // ====================== UTILS ======================
    private Integer parseRamValue(String ramStr) {
        if (ramStr == null || ramStr.isEmpty()) return 0;
        try {
            ramStr = ramStr.replaceAll("[^0-9.]", "").trim();
            if (ramStr.isEmpty()) return 0;
            double value = Double.parseDouble(ramStr);
            return (int) Math.round(value);
        } catch (Exception e) {
            System.err.println("Error parsing RAM value: " + ramStr);
            return 0;
        }
    }

    private String convertToString(Object obj) {
        if (obj == null) return "";
        if (obj instanceof String) return (String) obj;
        return obj.toString();
    }
}