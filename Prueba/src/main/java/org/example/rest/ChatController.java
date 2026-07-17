package org.example.rest;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ChatController {

	final ChatClient chatClient;

	@Value("classpath:/prompts/plantilla.st")
	Resource systemPrompt;

	@Autowired
	VectorStore vectorStore;

	public ChatController(ChatClient.Builder builder) {
		chatClient = builder
			.defaultAdvisors(new SimpleLoggerAdvisor())
			.build();
	}

	@PostMapping(value = "/api/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
		try {
			var message = request.message();

			if (message == null || message.isBlank()) {
				throw new IllegalArgumentException("El mensaje no puede estar vacio");
			}

			var searchRequest = SearchRequest.builder()
				.query(message)
				.topK(3)
				.build();

			List<Document> documentos = vectorStore.similaritySearch(searchRequest);
			String contexto = documentos.stream()
				.map(Document::getText)
				.reduce("", (a, b) -> a + "\n---\n" + b)
				.trim();

		List<String> sources = documentos.stream()
				.map(Document::getText)
				.toList();

			String answer = chatClient.prompt()
				.system(systemSpec -> systemSpec
					.text(systemPrompt)
					.param("contexto", contexto))
				.user(message)
				.call()
				.content();

			return ResponseEntity.ok(new ChatResponse(answer, sources));

		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ChatResponse(
				"Error: " + e.getMessage(),
				List.of()
			));
		}
	}

	@PostMapping(value = "/api/ingest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> ingestDocument(@RequestParam("file") MultipartFile file) {
		try {
			if (file.isEmpty()) {
				return ResponseEntity.badRequest().body("archivo basio");
			}

			List<Document> documentos;
			String filename = file.getOriginalFilename();

			if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
				Resource resource = new ByteArrayResource(file.getBytes());
				documentos = new PagePdfDocumentReader(resource).get();
			} else {
				Resource resource = new ByteArrayResource(file.getBytes());
				documentos = new TikaDocumentReader(resource).get();
			}
	List<Document> splitDocuments = splitDocuments(documentos, 300, 50);

			vectorStore.add(splitDocuments);

			return ResponseEntity.ok(new IngestResponse(
				"xito",
				"documento ingestado exitosamente ",
				splitDocuments.size(),
				filename
			));

		} catch (IOException e) {
			return ResponseEntity.status(500).body(new IngestResponse(
				"error",
				"error de procesp: " + e.getMessage(),
				0,
				file.getOriginalFilename()
			));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new IngestResponse(
				"error",
				"expected error: " + e.getMessage(),
				0,
				file.getOriginalFilename()
			));
		}
	}

	private List<Document> splitDocuments(List<Document> documentos, int chunkSize, int overlapSize) {
		List<Document> result = new ArrayList<>();

		for (Document doc : documentos) {
			String text = doc.getText();
			Map<String, Object> metadata = new HashMap<>(doc.getMetadata());

			List<String> chunks = splitText(text, chunkSize, overlapSize);

			for (int i = 0; i < chunks.size(); i++) {
				Map<String, Object> chunkMetadata = new HashMap<>(metadata);
				chunkMetadata.put("chunk_index", i);
				chunkMetadata.put("total_chunks", chunks.size());

				result.add(new Document(chunks.get(i), chunkMetadata));
			}
		}

		return result;
	}

	private List<String> splitText(String text, int chunkSize, int overlapSize) {
		List<String> chunks = new ArrayList<>();
		int comienzo = 0;

		while (comienzo < text.length()) {
			int end = Math.min(comienzo + chunkSize, text.length());

			if (end < text.length()) {
				int lastSpace = text.lastIndexOf(' ', end);
				if (lastSpace > comienzo) {
					end = lastSpace;
				}
			}

			if (end > comienzo) {
				chunks.add(text.substring(comienzo, end).trim());
			}

			comienzo = end;
			if (comienzo < text.length() && overlapSize > 0 && chunks.size() > 0) {
				comienzo = Math.max(comienzo - overlapSize, chunks.get(chunks.size() - 1).length() - overlapSize);
			}
		}

		return chunks;
	}
}

record ChatRequest(String message) {}

record ChatResponse(String answer, List<String> sources) {}

record IngestResponse(String status, String message, int chunksProcessed, String filename) {}
