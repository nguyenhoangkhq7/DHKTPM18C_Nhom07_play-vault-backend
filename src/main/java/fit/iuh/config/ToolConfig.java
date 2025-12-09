package fit.iuh.config;

import fit.iuh.services.GameBasicInfoService;
import fit.iuh.tools.GameTools;
import fit.iuh.tools.KnowledgeTools;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolConfig {

    @Bean
    public GameTools gameTools(GameBasicInfoService gameBasicInfoService) {
        return new GameTools(gameBasicInfoService);
    }

    @Bean
    public KnowledgeTools knowledgeTools(VectorStore vectorStore) {
        return new KnowledgeTools(vectorStore);
    }
}
