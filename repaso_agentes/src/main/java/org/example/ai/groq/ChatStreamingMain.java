package org.example.ai.groq;

import org.example.ai.groq.utils.MyStreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

/**
 * 08_CHAT STREAMING (Respuestas en Tiempo Real)
 * ============================================
 * 
 * PROPÓSITO:
 *   Recibir respuesta del modelo palabra por palabra conforme se genera.
 *   No esperar a que termine toda la generación.
 *
 * ¿POR QUÉ?
 *   - Mejor UX: usuario ve texto conforme aparece
 *   - Feedback inmediato: siente que app está respondiendo
 *   - Modelos grandes: pueden tardar 10-30 segundos
 *   - Streaming: primeras palabras en <1 segundo
 *
 * COMPARACIÓN:
 *   Regular Chat (blocking):
 *     Usuario: "¿Qué es...?"
 *     App: espera 20s...
 *     Usuario: "¿Todavía está procesando?"
 *   
 *   Streaming Chat:
 *     Usuario: "¿Qué es...?"
 *     App: "Un..." (1s después)
 *     App: "Un embedding..." (2s)
 *     App: "Un embedding es..." (3s)
 *     ← Usuario ve texto en tiempo real
 *
 * DIFERENCIA CON CHATMAIN:
 *   - ChatMain:        ChatModel → chat() → retorna String
 *   - ChatStreaming:   OpenAiStreamingChatModel → chat(msg, handler)
 *   - Handler recibe callbacks conforme llegan los chunks
 *
 * ARQUITECTURA:
 *   1. Crear OpenAiStreamingChatModel (en lugar de regular ChatModel)
 *   2. Crear handler que implementa StreamingChatResponseHandler
 *   3. Llamar chat(mensaje, handler)
 *   4. Handler recibe callbacks:
 *      - onPartialResponse(): nuevo chunk disponible
 *      - onCompleteResponse(): generación completada
 *      - onError(): error en la generación
 *
 * VER: MyStreamingChatResponseHandler.java para implementación del handler
 */
public class ChatStreamingMain {
    public static void main(String[] args) {
        // Cargar configuración
        var config = org.example.ai.groq.config.LlmConfigLoader.loadGroqConfig();

        // Crear streaming model con la configuración
        var chatModel = GroqClientFactory.buildStreamingModel(config, config.modelName());

        // Crear handler para procesar streaming
        var handler = new MyStreamingChatResponseHandler();

        // Enviar mensaje en streaming con manejo de fallback
        System.out.println("Pregunta: ¿Qué es llama.cpp?");
        System.out.println("Respuesta en streaming:");
        System.out.println("---");
        try {
            chatModel.chat("¿Que es llama.cpp?", handler);
        } catch (dev.langchain4j.exception.HttpException he) {
            System.err.println("HttpException en streaming: " + he.getMessage());
            if (GroqUtils.isModelDecommissioned(he) && config.fallbackModels() != null) {
                for (String fallback : config.fallbackModels()) {
                    try {
                        System.out.println("Reintentando streaming con fallback: " + fallback);
                        var newModel = GroqClientFactory.buildStreamingModel(config, fallback);
                        newModel.chat("¿Que es llama.cpp?", handler);
                        break;
                    } catch (Exception ex) {
                        System.err.println("Fallback streaming " + fallback + " falló: " + ex.getMessage());
                    }
                }
            } else {
                he.printStackTrace();
            }
        }

        // Paso 4: Keep alive
        System.out.println("(Esperando respuesta...)");
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
        
        // SALIDA EN TIEMPO REAL:
        // ---
        // llama.cpp es un proyecto...
        // que permite ejecutar...
        // modelos de lenguaje LLaMA...
        // [Generación completa]
        
        // MEJORAS FUTURAS:
        // - Usar CountDownLatch para esperar elegantemente
        // - Capturar toda la respuesta en handler.fullResponse
        // - Mostrar barra de progreso
        // - Cancelar si llega a máx tokens
    }
}
