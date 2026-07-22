package com.programacion.taller3.rest;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
public class ChatController {

        final ChatClient chatClient;

        @Value("classpath:/prompts/systemPrompt.st")
        Resource systemPrompt;

        @Autowired
        VectorStore vectorStore;

        public ChatController(ChatClient.Builder builder) {
                chatClient = builder
                             
                                .defaultAdvisors(new SimpleLoggerAdvisor())
                                .build();
        }

        private String searchDocuments(String query) {
                var request = SearchRequest.builder()
                                .query(query)
                                .topK(10)
                                .build();

                var documents = vectorStore.similaritySearch(request);

                return documents.stream()
                                .map(Document::getText)
                                .reduce("", (a, b) -> a + System.lineSeparator() + b)
                                .trim();
        }

        @PostMapping(value = "/api/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        public Flux<ServerSentEvent<String>> streamChat(@RequestBody ChatRequest request) {

                var message = request.message();

                if (message == null || message.isBlank()) {
                        throw new IllegalArgumentException("El mensaje no puede estar vacio");
                }

        
                String contexto = searchDocuments(message);

        
                var qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(
                                                SearchRequest.builder()
                                                                .query(message)
                                                                .topK(3)
                                                                .build())
                                .build();

                var chatMemoryAdvisor = VectorStoreChatMemoryAdvisor.builder(vectorStore).build();

                Flux<ServerSentEvent<String>> tokens = chatClient.prompt()
                                .system(systemSpec -> systemSpec
                                                .text(systemPrompt)
                                                .param("normativa", contexto))
                                .user(message)
                                .advisors(qaAdvisor, chatMemoryAdvisor)
                                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "default-conversation"))
                                .stream()
                                .content()
                                .map(chunk -> ServerSentEvent.<String>builder(chunk)
                                                .event("token")
                                                .data(
                                                                Base64.getEncoder().encodeToString(
                                                                                chunk.getBytes(StandardCharsets.UTF_8)))
                                                .build());

                Flux<ServerSentEvent<String>> done = Flux.just(
                                ServerSentEvent.<String>builder()
                                                .event("donde")
                                                .data("[DONE]")
                                                .build());

                return tokens.concatWith(done)
                                .onErrorResume(error -> Flux.just(
                                                ServerSentEvent.<String>builder()
                                                                .event("error")
                                                                .data(error.getMessage())
                                                                .build()));

        }

}
