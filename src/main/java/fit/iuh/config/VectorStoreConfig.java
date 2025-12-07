package fit.iuh.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

@Configuration
public class VectorStoreConfig {

    // File này sẽ được tạo tự động ở thư mục gốc của dự án
    private static final String VECTOR_STORE_FILE = "vector_store.json";

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        // Khởi tạo SimpleVectorStore với EmbeddingModel (Ollama tự động cung cấp)
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();

        // Kiểm tra nếu file đã tồn tại thì load dữ liệu cũ lên
        File file = new File(VECTOR_STORE_FILE);
        if (file.exists()) {
            vectorStore.load(new FileSystemResource(file));
            System.out.println("✅ Đã tải dữ liệu Vector từ file: " + file.getAbsolutePath());
        } else {
            System.out.println("⚠️ Chưa có file Vector, sẽ tạo mới khi lưu.");
        }
        return vectorStore;
    }
}