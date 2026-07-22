package taller.rest;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import taller.rest.ChatRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import java.util.List;
import java.util.ArrayList;

/**
 * Controlador REST que expone endpoints para interactuar con el LLM.
 * 
 * Inyecta las herramientas (Tools) provistas por los servidores MCP conectados y las configura
 * en el ChatClient predeterminado de Spring AI.
 */
@RestController
public class ChatController {

  // Cliente principal de interacción con el Modelo de IA
  final ChatClient chatClient;

  // Plantilla de prompt del sistema cargada desde los recursos del proyecto
  @Value("classpath:/prompts/systemPrompt.st")
  Resource systemPrompt;

  /**
   * Constructor que configura el ChatClient.
   * 
   * @param builder Builder inyectado automáticamente por Spring AI.
   * @param toolProviders Lista de proveedores de herramientas MCP detectados automáticamente en el contexto de Spring.
   */
  public ChatController(ChatClient.Builder builder, List<ToolCallbackProvider> toolProviders) {
    // Extrae todas las callback de herramientas provistas por los servidores MCP conectados
    List<ToolCallback> tools = new ArrayList<>();
    for (ToolCallbackProvider provider : toolProviders) {
        tools.addAll(List.of(provider.getToolCallbacks()));
    }

    // Registra el advisor de logs y todas las herramientas MCP encontradas
    chatClient = builder
            .defaultAdvisors(
                    new SimpleLoggerAdvisor()
            )
            .defaultTools(tools.toArray(new Object[0]))
            .build();
  }

  /**
   * Endpoint sincrónico / chat directo.
   * 
   * Recibe la pregunta del usuario en JSON y retorna la respuesta final completa del LLM en formato texto.
   */
  @PostMapping(value = "/chat", consumes = "application/json", produces = "text/plain")
  public String chat(@RequestBody ChatRequest request) {
    return chatClient.prompt()
            .system(systemSpec -> systemSpec
                            .text(systemPrompt)
            )
            .user(request.message())
            .call()
            .content();
  }

  /**
   * Endpoint asincrónico y reactivo vía Server-Sent Events (SSE).
   * 
   * Transmite la respuesta del LLM en tiempo real token por token codificados en Base64,
   * permitiendo que el cliente web reciba texto fluido y maneje eventos de finalización ([DONE]) o error.
   */
  @PostMapping(path = "/api/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<String>> chatStream(@RequestBody ChatRequest request) {

    var message = request.message();

    if (message == null || message.isBlank()) {
      throw new IllegalArgumentException("El mensaje no puede estar vacío");
    }

    // Transmisión de tokens individuales codificados en Base64
    Flux<ServerSentEvent<String>> tokens = chatClient.prompt()
            .system(systemSpec -> systemSpec
                    .text(systemPrompt)
            )
            .user(request.message())
            .stream()
            .content()
            .map(chunk -> ServerSentEvent.<String>builder(chunk)
                    .event("token")
                    .data(Base64.getEncoder().encodeToString(chunk.getBytes(StandardCharsets.UTF_8)))
                    .build()
            );

    // Evento enviado al finalizar la respuesta del LLM
    Flux<ServerSentEvent<String>> endEvent = Flux.just(
            ServerSentEvent.<String>builder()
                    .event("donde")
                    .data("[DONE]")
                    .build()
    );

    // Concatena los tokens con el evento final y captura posibles errores
    return tokens.concatWith(endEvent)
            .onErrorResume(error -> Flux.just(
                    ServerSentEvent.<String>builder()
                            .event("error")
                            .data(error.getMessage())
                            .build()
            ));

  }

}

