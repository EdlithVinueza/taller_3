package com.programacion.correcionprueba.rest;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ChatController {

    //cliente que porporciona spring par acomunicarse con el modelo -->se inicializa en el contructor
    private final ChatClient chatClient;

    //carga la paltilla del prompt del sistema desde el archivo prompt.st ubicado en la carpeta resources/prompts
    @Value("classpath:/prompts/prompt.st")
    private Resource systemPrompt;

    //inyecta el bean de VectorStore para interactuar con la base de datos vectorial Qdrant
    @Autowired
    private VectorStore vectorStore;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    //enpoitn que recibe y procesa el pdf
    @PostMapping(value = "/api/ingest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> ingest(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo no puede estar vacío");
        }

        File tempFile = null;
        try {

            Resource resource = file.getResource();

            // Leemos el PDF utilizando PagePdfDocumentReader
            PagePdfDocumentReader reader = new PagePdfDocumentReader(resource);
            List<Document> documents = reader.get();

            // Dividimos el documento utilizando TokenTextSplitter con chunk size de 300
            TokenTextSplitter splitter = TokenTextSplitter.builder()
                    .withChunkSize(300)
                    .build();
            List<Document> splitDocuments = splitter.split(documents);

            // Persistimos en la base de datos vectorial Qdrant
            vectorStore.add(splitDocuments);

            return ResponseEntity.ok("Archivo procesado y guardado con éxito. Chunks creados: " + splitDocuments.size());


        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el archivo PDF: " + e.getMessage());
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    @PostMapping(value = "/api/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String message = request.message();
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body(new ChatResponse("El mensaje no puede estar vacío", List.of()));
        }

        try {
            // 1. Retrieval manual con vectorStore.similaritySearch y topK = 3
            var searchRequest = SearchRequest.builder()
                    .query(message)
                    .topK(3)
                    .build();
            List<Document> matchedDocuments = vectorStore.similaritySearch(searchRequest);

            // 2. Extraer textos de los fragmentos y unirlos para el contexto
            String contexto = matchedDocuments.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining(System.lineSeparator()));

            // Las fuentes del JSON son los textos de esos mismos 3 chunks
            List<String> sources = matchedDocuments.stream()
                    .map(Document::getText)
                    .collect(Collectors.toList());

            // 3. Generar la respuesta usando el modelo de lenguaje de forma sincrónica
            String answer = chatClient.prompt()
                    .system(systemSpec -> systemSpec
                            .text(systemPrompt)
                            .param("contexto", contexto))
                    .user(message)
                    .call()
                    .content();

            return ResponseEntity.ok(new ChatResponse(answer, sources));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChatResponse("Error al procesar el chat: " + e.getMessage(), List.of()));
        }
    }
}
