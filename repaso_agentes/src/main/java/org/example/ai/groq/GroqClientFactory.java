package org.example.ai.groq;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.example.ai.groq.config.GroqConfig;

/**
 * Factory para crear clientes (sync y streaming) configurados para Groq
 */
public class GroqClientFactory {

    public static ChatModel buildChatModel(GroqConfig config, String modelName) {
        return OpenAiChatModel.builder()
                .apiKey(config.apiKey())
                .baseUrl(config.baseUrl())
                .modelName(modelName)
                .logRequests(config.logRequests())
                .logResponses(config.logResponses())
                .build();
    }

    public static OpenAiStreamingChatModel buildStreamingModel(GroqConfig config, String modelName) {
        return OpenAiStreamingChatModel.builder()
                .apiKey(config.apiKey())
                .baseUrl(config.baseUrl())
                .modelName(modelName)
                .logRequests(config.logRequests())
                .logResponses(config.logResponses())
                .build();
    }
}

