package com.programacion.taller3;

import com.programacion.taller3.utils.MyStreamingChatResponseHandler;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import static com.programacion.taller3.ChatMain.chatModel;

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
    static void main() {
        // Paso 1: Crear StreamingChatModel
        // (en lugar de regular ChatModel)
        var chatModel = OpenAiStreamingChatModel.builder()
                .apiKey("api-key-real")
                .modelName("llama-2-7b-chat.Q4_0.gguf")
                .baseUrl("http://localhost:8080")
                .logRequests(true)
                .logResponses(true)
                .build();

        // Paso 2: Crear handler para procesar streaming
        var handler = new MyStreamingChatResponseHandler();

        // Paso 3: Enviar mensaje con handler
        // Esto NO espera la respuesta completa
        // Handler recibe callbacks conforme llegan chunks
        System.out.println("Pregunta: ¿Qué es llama.cpp?");
        System.out.println("Respuesta en streaming:");
        System.out.println("---");
        
        chatModel.chat("¿Que es llama.cpp?", handler);

        // Paso 4: Keep alive
        // Necesitamos mantener la aplicación corriendo
        // mientras llegan los chunks del servidor
        // Este loop dummy simula trabajo mientras se procesa
        System.out.println("(Esperando respuesta...)");
        while (true) {
            // En producción: 
            // - Usar CompletableFuture
            // - Usar CountDownLatch
            // - Usar callback async
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
