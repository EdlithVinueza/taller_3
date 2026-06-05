package org.example.ai.groq;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;

import java.util.Scanner;

/**
 * 07_CHAT CON MEMORIA (Conversaciones Contextuales)
 * ================================================
 * 
 * PROPÓSITO:
 *   Mantener conversación multi-turno donde el modelo recuerda mensajes anteriores.
 *   Implementar chatbot con contexto real.
 *
 * ¿POR QUÉ?
 *   - Sin memoria: cada mensaje es independiente
 *   - Con memoria: conversaciones naturales como con humano
 *   - El modelo tiene acceso al historial de chat
 *
 * TIPOS DE MEMORIA:
 *   - MessageWindowChatMemory: últimos N mensajes (USAR)
 *   - TokenWindowChatMemory: últimos N tokens
 *   - ChatMemoryStore: persistencia en BD
 *
 * PROCESO:
 *   1. Crear ChatMemory con window=10 (recordar últimos 10 mensajes)
 *   2. Usar AiServices.builder() (no .create())
 *   3. Inyectar .chatMemory(memory)
 *   4. En loop: leer input → enviar al bot → mostrar respuesta
 *   5. AiServices automáticamente inyecta el historial en cada llamada
 *
 * FLUJO:
 *   Usuario: "¿Cuál es la capital de Ecuador?"
 *   → Memoria: [mensaje1]
 *   
 *   Usuario: "¿Cuántos habitantes tiene?"
 *   → Modelo recibe: [mensaje1, mensaje2] + contexto
 *   → Entiende que "tiene" se refiere a Quito
 *   → Respuesta coherente
 *
 * LIMITACIONES:
 *   - MessageWindowChatMemory solo en RAM (se pierde al cerrar)
 *   - Limite de tokens: modelos tienen max tokens (2K, 4K, etc.)
 *   - Con ventana grande: requests más lentos
 *
 * PRODUCCIÓN:
 *   - ChatMemoryStore: guardar en BD (MongoDB, PostgreSQL, etc.)
 *   - Implementar user sessions
 *   - Limpiar memoria periódicamente
 */
interface Conversador {
    /**
     * Método que será implementado por AiServices
     * AiServices inyecta automáticamente el ChatMemory
     */
    String chat(String mensaje);
}

public class ChatMemoryMain {
    public static void main(String[] args) {
        // Paso 1: Obtener configuración
        var config = org.example.ai.groq.config.LlmConfigLoader.loadGroqConfig();

        // Crear modelo inicial
        var model = GroqClientFactory.buildChatModel(config, config.modelName());

        // Paso 2: Crear memoria de chat
        // maxMessages(10): recordar últimos 10 mensajes
        ChatMemory memory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();

        // Paso 3: Crear bot con AiServices.builder()
        // Nota: builder() en lugar de create()
        // Razón: necesitamos inyectar la memoria
        Conversador bot = AiServices.builder(Conversador.class)
                .chatMemory(memory)              // Inyectar memoria
                .chatModel(model)                // Inyectar modelo
                .build();

        // Paso 4: Loop interactivo de conversación
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== CHATBOT CON MEMORIA ===");
        System.out.println("Escribe mensajes. Escribe 'exit' para salir.");
        System.out.println();

        while (true) {
            System.out.print("Tú: ");
            String msg = scanner.nextLine();

            // Verificar exit
            if("exit".equalsIgnoreCase(msg)) {
                System.out.println("¡Adiós!");
                break;
            }

            // Enviar mensaje al bot
            // AiServices automáticamente:
            // 1. Obtiene el historial de memory
            // 2. Inyecta el historial en el prompt del sistema
            // 3. Llama al modelo
            // 4. Guarda la respuesta en memory
            // 5. Retorna la respuesta
            try {
                var respuesta = bot.chat(msg);
                System.out.println("Bot: " + respuesta);
            } catch (dev.langchain4j.exception.HttpException he) {
                System.err.println("HttpException al llamar al bot: " + he.getMessage());
                // Si es model_decommissioned, intentar fallbacks
                if (GroqUtils.isModelDecommissioned(he)) {
                    var fallbacks = config.fallbackModels();
                    boolean switched = false;
                    if (fallbacks != null) {
                        for (String fallbackModel : fallbacks) {
                            try {
                                System.out.println("Reintentando con modelo fallback: " + fallbackModel);
                                var newModel = GroqClientFactory.buildChatModel(config, fallbackModel);
                                bot = AiServices.builder(Conversador.class)
                                        .chatMemory(memory)
                                        .chatModel(newModel)
                                        .build();
                                var respuesta = bot.chat(msg);
                                System.out.println("Bot: " + respuesta);
                                switched = true;
                                break;
                            } catch (Exception ex) {
                                System.err.println("Fallback " + fallbackModel + " falló: " + ex.getMessage());
                            }
                        }
                    }
                    if (!switched) {
                        System.err.println("No se pudo recuperar con fallbacks. Error original: " + he.getMessage());
                    }
                } else {
                    he.printStackTrace();
                }
            }
            System.out.println();
        }
        
        scanner.close();
        
        // MEJORAS FUTURAS:
        // - Agregar @SystemMessage("Eres un asistente amable...")
        // - Usar ChatMemoryStore para persistencia
        // - Multi-user: ChatMemory por usuario
        // - Analytics: log qué preguntas hace cada usuario
    }
}
