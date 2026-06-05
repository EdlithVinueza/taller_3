package com.programacion.taller3;

import com.programacion.taller3.utils.AsistenteLegal;
import dev.langchain4j.service.AiServices;

/**
 * 06_SERVICIOS DE IA (AiServices + Decoradores)
 * ============================================
 * 
 * PROPÓSITO:
 *   Crear servicios de IA usando decoradores en interfaces.
 *   Patrón más profesional y reutilizable que ChatMain.
 *
 * ¿POR QUÉ?
 *   - Separación: definiciones (@SystemMessage) del uso (métodos)
 *   - Type-safety: IDE sabe qué métodos existen
 *   - Reutilizable: múltiples instancias del mismo servicio
 *   - Testeable: fácil de mockear la interfaz
 *
 * PATRÓN DECORATOR:
 *   @SystemMessage("instrucción global") - contexto para el modelo
 *   @UserMessage("instrucción por método")  - prompt específico
 *   @V("variable")                          - inyecta parámetro en prompt
 *
 * CÓMO FUNCIONA:
 *   1. Define interfaz AsistenteLegal con métodos
 *   2. Decora cada método con @SystemMessage y @UserMessage
 *   3. Llama AiServices.create() con la interfaz y modelo
 *   4. AiServices genera implementación en tiempo de ejecución (reflection)
 *   5. Llama el método como si fuera un objeto normal
 *
 * VER: AsistenteLegal.java para ver la interfaz decorada
 */
public class ChatAiServiceMain {

    static void main() {
        // Paso 1: Obtener modelo de chat (reutilizamos ChatMain.chatModel())
        var model = ChatMain.chatModel();

        // Paso 2: Crear servicio usando AiServices
        // AiServices.create() genera una implementación de AsistenteLegal
        // en tiempo de ejecución que:
        // - Inyecta @SystemMessage
        // - Inyecta @UserMessage
        // - Inyecta parámetros con @V("nombre")
        // - Llama al modelo
        // - Retorna resultado
        var asistente = AiServices.create(AsistenteLegal.class, model);

        // Paso 3: Usar el servicio como un objeto normal
        System.out.println("=== MÉTODO: consultar() ===");
        System.out.println("Pregunta: ¿Cuál es el plazo de la preescripcion para deudas civiles?");
        
        var respuesta = asistente.consultar("Cuál es el plazo de la preescripcion para para deudas civiles?");
        System.out.println("Respuesta: " + respuesta);

        // Separador
        System.out.println("\n" + "=".repeat(50) + "\n");

        // Paso 4: Usar otro método con diferente @SystemMessage
        System.out.println("=== MÉTODO: responder() ===");
        System.out.println("Pregunta: ¿Cuál es el plazo de la preescripcion para deudas civiles?");
        
        respuesta = asistente.responder("Cuál es el plazo de la preescripcion para para deudas civiles?");
        System.out.println("Respuesta: " + respuesta);
        
        // VENTAJAS DE ESTE PATRÓN:
        // 1. Múltiples métodos en un servicio
        // 2. Cada método con su propia instrucción del sistema
        // 3. Reutilizable: solo necesitas 1 modelo para N servicios
        // 4. Code-first: defines la interfaz, AiServices genera el resto
        // 5. Testeable: MockObject puede implementar la interfaz
        
        // COMPARACIÓN:
        // ChatMain.java:       imperativos, procedurales
        // ChatAiServiceMain:   declarativos, servicios
    }
}
