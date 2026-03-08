package com.summit.chat.config;

import com.summit.chat.Enum.ModelType;
import com.summit.chat.service.AI.Agent;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.IngestionResult;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
@Slf4j
public class AgentConfig {
    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;
    @Value("${langchain4j.open-ai.chat-model.base-url}")
    private String baseUrl;
    @Value("${agent.milvus.host:localhost}")
    private String milvusHost;
    @Value("${agent.milvus.port:19530}")
    private Integer milvusPort;
    @Value("${agent.milvus.collection-name:langchain4j_rag}")
    private String collectionName;
    @Value("${agent.milvus.dimension:1024}")
    private Integer dimension;
    @Value("${agent.retriever.max-results:3}")
    private Integer maxResults;
    @Value("${agent.retriever.min-score:0.7}")
    private Double minScore;
    @Value("${agent.document.path:docs}")
    private String documentPath;
    @Value("${agent.chat-model.model-name}")
    private String modelName;
    @Value("${agent.chat-model.timeout-minutes:1}")
    private Long timeoutMinutes;
    @Value("${agent.chat-model.temperature:0.0}")
    private Double temperature;
    @Value("${agent.memory.max-messages:20}")
    private  Integer maxMessages;

    @Autowired
    private AgentToolProvider agentToolProvider;

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return MilvusEmbeddingStore.builder()
                .host(milvusHost)
                .port(milvusPort)
                .collectionName(collectionName)
                .dimension(dimension)
                .build();
    }


    //构建检索器
    @Bean
    public ContentRetriever contentRetriever(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
    }


    @Bean
    public CommandLineRunner ingestDocs(EmbeddingStore<TextSegment> embeddingStore,EmbeddingModel embeddingModel) {
        return args -> {
            //构建索引
            java.io.File docDir = new java.io.File(documentPath);
            if (!docDir.exists()) {
                log.warn("文档目录 {} 不存在，跳过文档加载", documentPath);
                return;
            }
            if (!docDir.isDirectory()) {
                log.error("{} 不是目录，跳过文档加载", documentPath);
                return;
            }
            
            List<Document> docs = FileSystemDocumentLoader.loadDocuments(documentPath, new TextDocumentParser());
            if (docs.isEmpty()) {
                log.info("文档目录 {} 为空，没有文档需要加载", documentPath);
                return;
            }
            
            //索引文档
            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .build();
            ingestor.ingest(docs);

            log.info("成功加载 {} 个文档到向量库", docs.size());
        };
    }


    @Bean
    public OpenAiStreamingChatModel openAiStreamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(ModelType.QWEN_PLUS.getModelName())
                .timeout(Duration.ofMinutes(timeoutMinutes))
                .temperature(temperature)
                .build();

    }

    //嵌入模型
    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .baseUrl(baseUrl)
                .timeout(Duration.ofMinutes(timeoutMinutes))
                .build();
    }


    @Bean
    public Agent agent(ContentRetriever contentRetriever,OpenAiStreamingChatModel openAiStreamingChatModel) {
        return AiServices.builder(Agent.class)
                .streamingChatModel(openAiStreamingChatModel)
                .chatMemoryProvider((id) -> new Memory().memory(id.toString()))
                .tools(agentToolProvider)
                .contentRetriever(contentRetriever)
                .build();
    }


     class Memory {
        public MessageWindowChatMemory memory(String id) {
            return MessageWindowChatMemory.builder()
                    .maxMessages(maxMessages)
                    .id(id)
                    .build();
        }


    }

}