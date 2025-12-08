package fit.iuh.tools;

import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.stream.Collectors;

public class KnowledgeTools {

    private final VectorStore vectorStore;

    public KnowledgeTools(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Tool(description = "Tra cứu thông tin về chính sách, câu hỏi thường gặp (FAQ), giới thiệu chung về PlayVault. Dùng công cụ này khi người dùng hỏi các câu hỏi không liên quan đến thông số kỹ thuật cụ thể của game.")
    public String searchGeneralInfo(String query) {
        // Tìm kiếm 4 đoạn văn bản liên quan nhất
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .similarityThreshold(0.5)
                        .topK(4)
                        .build()
        );

        if (docs.isEmpty()) {
            return "Không tìm thấy thông tin liên quan trong tài liệu.";
        }

        // Gom nội dung các tài liệu lại thành 1 chuỗi string để AI đọc
        return docs.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n---\n"));
    }
}