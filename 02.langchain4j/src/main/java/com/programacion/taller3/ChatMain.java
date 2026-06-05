package com.programacion.taller3;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

/**
 * 05_CHAT BÁSICO (Comunicación con LLM)
 * ====================================
 * 
 * PROPÓSITO:
 *   Establecer conexión con un modelo de lenguaje (LLM) y enviar mensajes simples.
 *   Es el "Hola Mundo" de LangChain4J.
 *
 * ¿POR QUÉ?
 *   - LLMs necesitan configuración (URL, modelo, API key)
 *   - LangChain4J abstrae estas complejidades
 *   - Método chatModel() es reutilizable en otros archivos
 *
 * ARQUITECTURA:
 *   Aplicación Java → LangChain4J → OpenAiChatModel → HTTP → Ollama/OpenAI
 *
 * CONFIGURACIÓN:
 *   - apiKey: "api-key-real" (dummy en Ollama local, ignora)
 *   - modelName: "llama-2-7b-chat.Q4_0.gguf" (modelo de Ollama)
 *   - baseUrl: "http://localhost:8080" (servidor Ollama local)
 *   - logRequests(true): muestra requests HTTP
 *   - logResponses(true): muestra responses HTTP
 *
 * REQUISITOS:
 *   ✓ Ollama debe estar corriendo en localhost:8080
 *   ✓ Modelo llama-2-7b-chat descargado en Ollama
 *   ✓ Java 11+
 *   ✓ LangChain4J en classpath
 *
 * CÓMO VERIFICAR:
 *   $ curl http://localhost:8080/api/tags
 *   (debe listar modelos disponibles)
 *
 * FLUJO:
 *   1. Crear builder con configuración
 *   2. Construir ChatModel
 *   3. Llamar chat(mensaje)
 *   4. Recibir y mostrar respuesta
 *
 * SALIDA:
 *   Respuesta completa del modelo (puede tardar 5-30s)
 */
public class ChatMain {
    
    /**
     * Factory method para crear ChatModel configurado
     * Reutilizable en otras clases (ChatMemoryMain, ChatAiServiceMain, etc.)
     * 
     * @return ChatModel configurado para conectar con Ollama local
     */
    public static ChatModel chatModel() {
        // Nota importante: aunque el nombre es OpenAiChatModel,
        // LangChain4J permite usarlo con cualquier servidor compatible
        // (Ollama, Hugging Face, Azure OpenAI, etc.)
        return OpenAiChatModel.builder()
                // API Key (dummy para Ollama local, no se valida)
                .apiKey("api-key-real")
                
                // Nombre del modelo en Ollama
                // Asegúrate de que esté descargado: ollama pull llama2
                .modelName("llama-2-7b-chat.Q4_0.gguf")
                
                // URL del servidor Ollama
                // Si usas OpenAI: "https://api.openai.com/v1"
                // Si no especificas, intenta conectar a OpenAI
                .baseUrl("http://localhost:8080")
                
                // Debug: mostrar requests HTTP enviados
                .logRequests(true)
                
                // Debug: mostrar responses HTTP recibidas
                .logResponses(true)
                
                .build();
    }
    
    /**
     * Método main: ejemplo de chat básico
     */
    static void main() {
        // Crear modelo de chat
        ChatModel model = chatModel();

        // Enviar mensaje y recibir respuesta
        // Este es un chat de UN turno (sin memoria)
        var respuesta = model.chat("Qué es llama.cpp?");

        // Mostrar resultado
        System.out.println("[RESPUESTA]");
        System.out.println(respuesta);
        
        // SALIDA ESPERADA (puede variar):
        // [RESPUESTA]
        // llama.cpp es un proyecto que implementa un motor de inferencia
        // eficiente en C++ para modelos de lenguaje LLaMA...
    }
}
