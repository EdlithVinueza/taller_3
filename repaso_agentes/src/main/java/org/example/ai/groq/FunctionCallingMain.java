package org.example.ai.groq;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

import java.time.LocalDateTime;

/**
 * 10_FUNCTION CALLING (Herramientas/Tools)
 * =======================================
 * 
 * PROPÓSITO:
 *   El modelo puede "llamar funciones" (tools/herramientas) para hacer cosas.
 *   El modelo decide cuándo y cuál función usar.
 *   El framework ejecuta la función y retorna el resultado al modelo.
 *
 * ¿POR QUÉ?
 *   - LLMs no pueden hacer cálculos reales
 *   - LLMs no tienen acceso a datos en tiempo real (hora, BD, APIs)
 *   - Necesitamos "extender" al modelo con funciones externas
 *   - El modelo decide qué función usar según el contexto
 *
 * CASOS DE USO:
 *   - "¿Qué hora es?" → Modelo llama obtenerFechaHora()
 *   - "¿Área de círculo radio 5?" → Modelo llama calcularAreaCirculo(5)
 *   - "Cuéntame sobre usuario 123" → Modelo llama buscarUsuario(123)
 *   - "Envía email a..." → Modelo llama enviarEmail()
 *   - "Haz una transferencia" → Modelo llama transferirDinero()
 *
 * FLUJO:
 *   1. Usuario pregunta: "¿Qué hora es?"
 *   2. AiServices envía al modelo:
 *      - Pregunta: "¿Qué hora es?"
 *      - Herramientas disponibles (JSON Schema de métodos @Tool)
 *   3. Modelo responde: "Necesito llamar obtenerFechaHora()"
 *   4. Framework ve: "oh, quiere llamar esta función"
 *   5. Framework ejecuta: obtenerFechaHora() → "2026-05-30T18:55:05"
 *   6. Framework envía resultado al modelo
 *   7. Modelo responde: "Son las 18:55:05"
 *   8. Usuario ve: "Son las 18:55:05"
 *
 * DECORADORES:
 *   @Tool("descripción")     → marca método como herramienta disponible
 *   @P("descripción")        → describe parámetro de función
 *   @SystemMessage()         → instrucción para el modelo
 *
 * RESTRICCIONES:
 *   - Nunca llamar funciones peligrosas sin confirmación
 *   - Implementar autorización
 *   - Logging de cada llamada
 *   - Timeout para seguridad
 */

class Herramientas {
    /**
     * Herramienta 1: Obtener hora actual
     * 
     * @Tool: marca que esto está disponible para el modelo
     * El modelo puede llamarla automáticamente
     */
    @Tool("Obtiene la fecha y hora actual")
    String obtenerFechaHora() {
        return LocalDateTime.now().toString();
    }

    /**
     * Herramienta 2: Calcular área de círculo
     * 
     * @Tool: marca disponibilidad
     * @P: describe qué es el parámetro
     * 
     * El modelo puede: "Calcula el área de un círculo con radio 5"
     * → Llamará calcularAreaCirculo(5.0)
     */
    @Tool("Calcula el área de un círculo dado su radio")
    double calcularAreaCirculo(@P("El radio del circulo") double radio) {
        return Math.PI * radio * radio;
    }
}

/**
 * Interfaz del asistente con tools
 * El modelo puede usar las herramientas disponibles
 */
interface AsistenteConTools {
    @SystemMessage("Eres un asistente util. Usa las herramientas disponibles")
    String chat(String mensaje);
}

public class FunctionCallingMain {
    public static void main(String[] args) {
        // Cargar configuración y crear modelo
        var config = org.example.ai.groq.config.LlmConfigLoader.loadGroqConfig();
        var model = GroqClientFactory.buildChatModel(config, config.modelName());

        // Crear asistente con tools
        var service = AiServices.builder(AsistenteConTools.class)
                .chatModel(model)
                .tools(new Herramientas())  // ← Inyectar herramientas
                .build();

        // Paso 3: Usar el asistente
        System.out.println("=== ASISTENTE CON HERRAMIENTAS ===\n");

        // PREGUNTA 1: que hora es
        System.out.println("Usuario: ¿Qué hora es?");
        try {
            var respuesta = service.chat("qué hora es?");
            System.out.println("Asistente: " + respuesta);
        } catch (dev.langchain4j.exception.HttpException he) {
            System.err.println("HttpException en FunctionCallingMain: " + he.getMessage());
            if (GroqUtils.isModelDecommissioned(he) && config.fallbackModels() != null) {
                boolean ok = false;
                for (String fallback : config.fallbackModels()) {
                    try {
                        var newModel = GroqClientFactory.buildChatModel(config, fallback);
                        service = AiServices.builder(AsistenteConTools.class).chatModel(newModel).tools(new Herramientas()).build();
                        var resp = service.chat("qué hora es?");
                        System.out.println("Asistente: " + resp);
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

        System.out.println();

        // PREGUNTA 2: calcular área
        System.out.println("Usuario: ¿Cuál es el área de un círculo de radio 5?");
        try {
            var respuesta = service.chat("Cual es el area de un circulo de radio 5?");
            System.out.println("Asistente: " + respuesta);
        } catch (dev.langchain4j.exception.HttpException he) {
            System.err.println("HttpException en FunctionCallingMain (pregunta 2): " + he.getMessage());
            he.printStackTrace();
        }

        // EXPLICACIÓN QUÉ SUCEDIÓ:
        System.out.println("=== QUÉ SUCEDIÓ DETRÁS ===");
        System.out.println("1. Usuario pregunta: '¿Qué hora es?'");
        System.out.println("2. AiServices envía al modelo:");
        System.out.println("   - Pregunta");
        System.out.println("   - JSON Schema de herramientas disponibles");
        System.out.println("3. Modelo responde:");
        System.out.println("   - Necesito llamar función obtenerFechaHora()");
        System.out.println("4. Framework ejecuta: obtenerFechaHora()");
        System.out.println("5. Framework retorna resultado al modelo");
        System.out.println("6. Modelo integra resultado en respuesta");
        System.out.println("7. Usuario recibe respuesta final");
        
        // FLUJO COMPLETO:
        // Usuario → Pregunta
        // ↓
        // AiServices → Envía al modelo (con herramientas disponibles)
        // ↓
        // Modelo → "Necesito calcularAreaCirculo(5)"
        // ↓
        // Framework → Ejecuta calcularAreaCirculo(5) = 78.54
        // ↓
        // AiServices → Envía resultado al modelo
        // ↓
        // Modelo → "El área es 78.54 unidades cuadradas"
        // ↓
        // Usuario → Recibe respuesta
        
        // SEGURIDAD IMPORTANTE:
        // - En producción, validar quién puede llamar cada herramienta
        // - Agregar autenticación y autorización
        // - Loguear cada llamada
        // - Implementar rate limiting
        // - Usar try-catch para manejo de errores
    }
}
