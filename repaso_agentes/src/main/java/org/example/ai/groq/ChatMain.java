package org.example.ai.groq;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.example.ai.groq.config.LlmConfigLoader;

/**
 * 05_CHAT BÁSICO CON GROQ (Comunicación con LLM)
 * ====================================
 * 
 * PROPÓSITO:
 *   Establecer conexión con Groq (proveedor de LLM rápido) y enviar mensajes simples.
 *   Groq ofrece API compatible con OpenAI, así que usamos OpenAiChatModel.
 *
 * ¿POR QUÉ GROQ?
 *   - Groq ofrece inferencia ultra rápida (>500 tokens/segundo)
 *   - Modelos de alta calidad (Mixtral, LLaMA, etc.)
 *   - API compatible con OpenAI (100% compatible)
 *   - Capa gratuita disponible
 *
 * ARQUITECTURA:
 *   Aplicación Java → LangChain4J → OpenAiChatModel → HTTP → Groq API
 *   (Functiona porque Groq implementa completamente la API de OpenAI)
 *
 * CONFIGURACIÓN:
 *   - apiKey: Tu API key de https://console.groq.com
 *   - modelName: Modelos disponibles (mixtral-8x7b-32768, llama2-70b-4096, etc.)
 *   - baseUrl: https://api.groq.com/openai/v1 (URL de Groq)
 *   - logRequests(true): muestra requests HTTP
 *   - logResponses(true): muestra responses HTTP
 *
 * REQUISITOS:
 *   ✓ Crear cuenta en https://console.groq.com
 *   ✓ Generar API key desde la consola
 *   ✓ Java 11+
 *   ✓ LangChain4J en classpath
 *
 * MODELOS DISPONIBLES:
 *   - mixtral-8x7b-32768 (Recomendado: rápido y capaz)
 *   - llama2-70b-4096 (Poderoso para tareas complejas)
 *   - llama3-70b-8192 (LLaMA 3 mejorado)
 *   - gemma-7b-it (Eficiente)
 *
 * FLUJO:
 *   1. Crear OpenAiChatModel con URL y API key de Groq
 *   2. Configurar nombre del modelo de Groq
 *   3. Llamar chat(mensaje)
 *   4. Recibir respuesta instantánea gracias a Groq
 *
 * SALIDA:
 *   Respuesta muy rápida (típicamente <1 segundo)
 */
public class ChatMain {
    
    /**
     * Factory method para crear ChatModel configurado con Groq
     * Reutilizable en otras clases (ChatMemoryMain, ChatAiServiceMain, etc.)
     * 
     * ✓ CONFIGURACIÓN CARGADA DESDE YAML:
     *   - src/main/resources/application.yml (configuración)
     *   - src/main/resources/secrets.yml (API keys)
     *
     * @return ChatModel configurado para conectar con Groq API
     */
    public static ChatModel chatModel() {
        // Cargar configuración desde YAML
        var config = LlmConfigLoader.loadGroqConfig();

        System.out.println("✓ Configuración cargada desde YAML:");
        System.out.println("  Base URL: " + config.baseUrl());
        System.out.println("  Modelo: " + config.modelName());
        System.out.println("  Log Requests: " + config.logRequests());
        System.out.println("  Log Responses: " + config.logResponses());

        // Construir ChatModel con la configuración del YAML
        return OpenAiChatModel.builder()
                // API key desde secrets.yml
                .apiKey(config.apiKey())

                // URL desde application.yml
                .baseUrl(config.baseUrl())

                // Modelo desde application.yml
                .modelName(config.modelName())

                // Debug: mostrar requests HTTP
                .logRequests(config.logRequests())

                // Debug: mostrar responses HTTP
                .logResponses(config.logResponses())

                .build();
    }
    
    /**
     * Método main: ejemplo de chat básico con Groq
     */
    public static void main(String[] args) {
        // Cargar configuración y usar safeChat (intenta fallbacks si es necesario)
        var config = org.example.ai.groq.config.LlmConfigLoader.loadGroqConfig();
        String respuesta = GroqUtils.safeChat(config, "¿Qué es un agente de IA?");

        // Mostrar resultado
        System.out.println("[RESPUESTA GROQ]");
        System.out.println(respuesta);
        
        // SALIDA ESPERADA (muy rápida):
        // [RESPUESTA GROQ]
        // Un agente de IA es un programa que puede percibir su entorno,
        // tomar decisiones y realizar acciones de forma autónoma...
    }
}
