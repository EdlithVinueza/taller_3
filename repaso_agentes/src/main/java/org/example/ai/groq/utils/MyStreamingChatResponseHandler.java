package org.example.ai.groq.utils;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.PartialResponse;
import dev.langchain4j.model.chat.response.PartialResponseContext;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * HANDLER PARA STREAMING: MyStreamingChatResponseHandler
 * ====================================================
 * 
 * Propósito: Procesar respuestas en streaming (palabra por palabra)
 * Usada por: ChatStreamingMain.java
 *
 * ¿QUÉ ES STREAMING?
 *   - Regular chat: espera respuesta completa (10-30 segundos)
 *   - Streaming: recibe respuesta palabra por palabra en tiempo real
 *   - UX: usuario ve texto mientras se genera (mejor experiencia)
 *
 * INTERFAZ StreamingChatResponseHandler:
 *   1. onPartialResponse(String) - nuevo chunk de texto disponible
 *   2. onPartialResponse(PartialResponse) - respuesta parcial con metadata
 *   3. onCompleteResponse(ChatResponse) - respuesta completada
 *   4. onError(Throwable) - error durante generación
 *
 * FLUJO:
 *   Servidor envía: "Un"
 *   → onPartialResponse("Un")  [imprime "Un"]
 *   
 *   Servidor envía: " embedding"
 *   → onPartialResponse(" embedding")  [imprime " embedding"]
 *   
 *   Servidor envía: " es..."
 *   → onPartialResponse(" es...")  [imprime " es..."]
 *   
 *   Servidor termina
 *   → onCompleteResponse(ChatResponse)  [imprime "[Generación completa]"]
 *
 * IMPLEMENTACIÓN ACTUAL:
 *   - onPartialResponse(String): imprime chunk + flush
 *   - onCompleteResponse(): imprime salto de línea + "[Generación completa]"
 *   - onError(): imprime stack trace
 *   - count: AtomicInteger para tracking (opcional)
 */
public class MyStreamingChatResponseHandler implements StreamingChatResponseHandler {
    
    // Contador atómico para tracking de respuestas (opcional)
    // AtomicInteger: thread-safe, puede decrementarse desde callbacks
    AtomicInteger count = null;
    
    public MyStreamingChatResponseHandler() {
        this.count = count;
    }

    /**
     * CALLBACK 1: Cada chunk de texto disponible
     * 
     * Se llama múltiples veces conforme llegan chunks desde el servidor
     * 
     * @param partialResponse el texto recibido (ej: "Un", " embedding", " es")
     * 
     * Implementación actual: imprimir sin salto de línea + flush
     * - print(): no agrega salto de línea
     * - flush(): fuerza escritura inmediata (no espera buffer)
     */
    @Override
    public void onPartialResponse(String partialResponse) {
        System.out.print(partialResponse);
        System.out.flush();
    }

    /**
     * CALLBACK 2: Respuesta parcial con contexto
     * 
     * Similar a onPartialResponse(String) pero con metadata
     * 
     * @param partialResponse objeto con más información
     * @param context contexto de la respuesta (tokens, etc.)
     * 
     * Implementación actual: delegar a implementación default
     */
    @Override
    public void onPartialResponse(PartialResponse partialResponse, PartialResponseContext context) {
        // Llamar implementación por defecto de la interfaz
        StreamingChatResponseHandler.super.onPartialResponse(partialResponse, context);
    }

    /**
     * CALLBACK 3: Respuesta completada
     * 
     * Se llama UNA VEZ cuando el modelo termina de generar
     * 
     * @param completeResponse respuesta completa con metadata
     * 
     * Implementación actual:
     * - Imprimir salto de línea (nueva línea)
     * - Imprimir "[Generación completa]" como indicador
     */
    @Override
    public void onCompleteResponse(ChatResponse completeResponse) {
        System.out.println();
        System.out.println("[Generación completa]");
        
        // NOTA: Hay bug potencial aquí
        // count es null (inicializado a null en constructor)
        // Llamar count.decrementAndGet() causaría NullPointerException
        // Debería ser:
        // if (count != null) count.decrementAndGet();
        // O inicializar: count = new AtomicInteger(1);
    }

    /**
     * CALLBACK 4: Error durante generación
     * 
     * Se llama si hay error en el servidor o conexión
     * 
     * @param error la excepción que ocurrió
     * 
     * Implementación actual: imprimir stack trace
     */
    @Override
    public void onError(Throwable error) {
        error.printStackTrace();
    }
    
    /**
     * MEJORAS FUTURAS:
     * 
     * 1. Capturar respuesta completa:
     *    private StringBuilder fullResponse = new StringBuilder();
     *    En onPartialResponse(): fullResponse.append(partialResponse);
     *    
     * 2. Contar tokens:
     *    Agregar contador en PartialResponseContext
     *    
     * 3. Timing:
     *    long startTime = System.currentTimeMillis();
     *    En onCompleteResponse(): calcular tiempo total
     *    
     * 4. Cancelación:
     *    Si token count > max, cancelar streaming
     *    
     * 5. Progreso visual:
     *    Barra de progreso
     *    Spinner animado
     */
}
