package fit.iuh.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document; // ⚠️ Kiểm tra kỹ dòng này
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AIService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Autowired
    public AIService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    public Flux<String> chatWithTool(String userMessage) {
        // --- 1. TẠO REQUEST TÌM KIẾM (Sửa theo hình ảnh bạn gửi) ---
        // Sử dụng Builder pattern thay vì defaults()
        SearchRequest request = SearchRequest.builder()
                .query(userMessage)       // Gõ dấu chấm và chọn query hoặc content
                .topK(3)                  // Lấy 3 kết quả
                .similarityThreshold(0.5) // Độ chính xác > 50%
                .build();

        // --- 2. GỌI VECTOR STORE ---
        List<Document> similarDocs = vectorStore.similaritySearch(request);

        // --- 3. LẤY NỘI DUNG (Sửa lỗi không có getContent) ---
        String contextData = similarDocs.stream()
                .map(doc -> {
                    // ⚠️ LƯU Ý: Nếu doc.getContent() báo đỏ, hãy thử các hàm sau:
                    // 1. doc.getText()
                    // 2. doc.toString()
                    // 3. doc.getMetadata().get("text")
                    // Ở đây tôi dùng .getText() vì đây là hàm phổ biến ở các bản cũ
                    return doc.getText();
                })
                .collect(Collectors.joining("\n\n"));

        // --- 4. GỌI AI (Streaming) ---
        return chatClient.prompt()
                .user(userMessage)
                .system(s -> s.text("""
                        Bạn là trợ lý AI của PlayVault.
                        
                        Hãy sử dụng thông tin ngữ cảnh bên dưới (nếu có) để trả lời câu hỏi:
                        =============================
                        {context_data}
                        =============================
                        
                        Nếu không có thông tin trong ngữ cảnh, hãy dùng Tool hoặc trả lời dựa trên kiến thức chung nhưng phải báo rõ nguồn.
                        Ngôn ngữ: Tiếng Việt.
                        Đơn vị tiền tệ: Gcoin.
                        Trình bày ngắn gọn, rõ ràng.
                        """)
                        .param("context_data", contextData.isEmpty() ? "Không có dữ liệu ngữ cảnh." : contextData)
                )
                .stream()
                .content();
    }
}