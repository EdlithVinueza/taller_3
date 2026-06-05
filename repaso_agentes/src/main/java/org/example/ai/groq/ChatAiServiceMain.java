package org.example.ai.groq;

import org.example.ai.groq.utils.AsistenteLegal;
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

    public static void main(String[] args) {
        // Cargar configuración y crear modelo
        var config = org.example.ai.groq.config.LlmConfigLoader.loadGroqConfig();
        var model = GroqClientFactory.buildChatModel(config, config.modelName());

        // Crear servicio
        var asistente = AiServices.create(AsistenteLegal.class, model);

        // Usar el servicio con manejo de errores y fallback
        System.out.println("=== MÉTODO: consultar() ===");
        System.out.println("Pregunta: ¿Cuál es el plazo de la preescripcion para deudas civiles?");
        try {
            var respuesta = asistente.consultar("Cuál es el plazo de la preescripcion para para deudas civiles?");
            System.out.println("Respuesta: " + respuesta);
        } catch (dev.langchain4j.exception.HttpException he) {
            System.err.println("HttpException en AiService: " + he.getMessage());
            if (GroqUtils.isModelDecommissioned(he) && config.fallbackModels() != null) {
                boolean ok = false;
                for (String fallback : config.fallbackModels()) {
                    try {
                        var newModel = GroqClientFactory.buildChatModel(config, fallback);
                        asistente = AiServices.create(AsistenteLegal.class, newModel);
                        var respuesta = asistente.consultar("Cuál es el plazo de la preescripcion para para deudas civiles?");
                        System.out.println("Respuesta: " + respuesta);
                        ok = true;
                        break;
                    } catch (Exception ex) {
                        System.err.println("Fallback " + fallback + " falló: " + ex.getMessage());
                    }
                }
                if (!ok) System.err.println("No se pudo recuperar con fallbacks.");
            } else {
                he.printStackTrace();
            }
        }

        // Separador
        System.out.println("\n" + "=".repeat(50) + "\n");

        System.out.println("=== MÉTODO: responder() ===");
        System.out.println("Pregunta: ¿Cuál es el plazo de la preescripcion para deudas civiles?");
        try {
            var respuesta = asistente.responder("Cuál es el plazo de la preescripcion para para deudas civiles?");
            System.out.println("Respuesta: " + respuesta);
        } catch (dev.langchain4j.exception.HttpException he) {
            System.err.println("HttpException en AiService.responder: " + he.getMessage());
            he.printStackTrace();
        }

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
