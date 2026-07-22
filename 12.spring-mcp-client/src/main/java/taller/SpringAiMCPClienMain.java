package taller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que inicializa la aplicación del Cliente MCP (Model Context Protocol).
 * 
 * Este cliente Spring Boot integra:
 * 1. Conexión a Servidores MCP (definidos en mcp-server.json vía STDIO).
 * 2. Integración con Modelos de Lenguaje (OpenAI / Groq con LLaMA 3.3 70B).
 * 3. Procesamiento de documentos RAG con Apache Camel (inyección/procesamiento de PDFs) y Qdrant Vector Store.
 * 4. Controladores REST para chat en tiempo real y streaming SSE.
 */
@SpringBootApplication
public class SpringAiMCPClienMain {
  public static void main(String[] args) {
    SpringApplication.run(SpringAiMCPClienMain.class, args);
  }
}

