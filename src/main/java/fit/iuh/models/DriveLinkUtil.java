package fit.iuh.models;

public final class DriveLinkUtil {
    private DriveLinkUtil() {}

    public static String toLh3Url(String fileId) {
        return "https://lh3.googleusercontent.com/d/" + fileId + "=w1200";
    }

    public static String extractFileId(String urlOrId) {
        if (urlOrId == null || urlOrId.isBlank()) return null;
        if (!urlOrId.startsWith("http") && urlOrId.matches("[\\w-]{20,}")) return urlOrId;
        var m = java.util.regex.Pattern.compile("(?:id=|/d/)([A-Za-z0-9_-]{20,})").matcher(urlOrId);
        return m.find() ? m.group(1) : null;
    }

    /** Nếu req.getThumbnail() là link uc?id=.../file/d/... → đổi sang lh3; nếu không phải link Drive thì trả nguyên */
    public static String toEmbeddableIfDriveUrl(String urlOrId) {
        String id = extractFileId(urlOrId);
        return (id == null) ? urlOrId : toLh3Url(id);
    }
}
