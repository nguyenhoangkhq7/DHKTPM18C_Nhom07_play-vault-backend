package fit.iuh.services;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentVectorService {
    @Autowired
    private VectorStore vectorStore;

    @PostConstruct
    public void loadTextDocuments() throws IOException {
        // Load file 1
        Resource resource1 = new ClassPathResource("documents/game_policy.txt");
        String chinhSach = new String(resource1.getInputStream().readAllBytes());
        Document doc1 = new Document(
                UUID.nameUUIDFromBytes("policy_doc_001".getBytes()).toString(),
                chinhSach,
                Map.of("type", "policy", "source", "hotel_policy.txt", "category", "terms")
        );
        vectorStore.add(List.of(doc1));

        // Load file 2
        Resource resource2 = new ClassPathResource("documents/game_introduce.txt");
        String gioiThieu = new String(resource2.getInputStream().readAllBytes());
        Document doc2 = new Document(
                UUID.nameUUIDFromBytes("intro_doc_002".getBytes()).toString(),
                gioiThieu,
                Map.of("type", "introduction", "source", "game_introduce.txt", "category", "game_info")
        );
        vectorStore.add(List.of(doc2));

        Resource resource3 = new ClassPathResource("documents/game_faq.txt");
        // Kiểm tra file tồn tại để tránh lỗi FileNotFoundException
        if (resource3.exists()) {
            String faqContent = new String(resource3.getInputStream().readAllBytes());
            Document doc3 = new Document(
                    UUID.nameUUIDFromBytes("faq_doc_003".getBytes()).toString(),
                    faqContent,
                    Map.of("type", "faq", "source", "game_faq.txt", "category", "support")
            );
            vectorStore.add(List.of(doc3));
        }
    }
}
