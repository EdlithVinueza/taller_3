package com.programacion.taller3;

import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 09_SALIDA ESTRUCTURADA (Extracción de Datos)
 * ============================================
 * 
 * PROPÓSITO:
 *   Extraer información estructurada (JSON/Record) del texto generado por el modelo.
 *   Convertir respuesta de texto libre en objeto Java tipado.
 *
 * ¿POR QUÉ?
 *   - Raw text: "Soy María, tengo 30 años, vivo en Quito"
 *     → Difícil de procesar programáticamente
 *     → Parsing manual: regex, split, etc.
 *     → Propenso a errores
 *
 *   - Structured output: Persona(nombre="María", edad=30, ciudad="Quito")
 *     → Fácil de procesar
 *     → Type-safe
 *     → IDE autocomplete
 *     → Serializable a JSON/BD
 *
 * CASOS DE USO:
 *   - NER (Named Entity Recognition)
 *   - Clasificación de documentos
 *   - Extracción de datos de CVs
 *   - Parsing de facturas
 *   - Información de contacto
 *
 * PROCESO:
 *   1. Definir Record con campos deseados
 *   2. Definir interfaz con método que retorna Record
 *   3. Decorar método con @UserMessage
 *   4. Usar @V("variable") para inyectar parámetros
 *   5. Usar AiServices.create()
 *   6. Llamar el método
 *   7. Recibir objeto Record ya parseado
 *
 * CÓMO FUNCIONA INTERNAMENTE:
 *   1. Definición de Record: Persona { nombre, edad, ciudad }
 *   2. AiServices genera JSON Schema del Record
 *   3. Envía al modelo junto con el prompt:
 *      "Extrae la información y retorna JSON con campos: nombre, edad, ciudad"
 *   4. Modelo retorna JSON:
 *      {"nombre": "María", "edad": 30, "ciudad": "Quito"}
 *   5. AiServices parsea JSON y retorna objeto Persona
 *
 * VENTAJAS:
 *   - Zero configuration: Record → JSON Schema automático
 *   - Type-safe: compilación en tiempo de compilación
 *   - Fácil: UI puede mostrar valores directamente
 *   - Validable: null checks, ranges, etc.
 */

// Paso 1: Definir Record de salida estructurada
record Persona(String nombre, int edad, String ciudad) {}

// Paso 2: Definir interfaz del extractor
interface Extractor {
    /**
     * Extrae información de persona de un texto libre
     * 
     * @UserMessage especifica el prompt
     * {{texto}} es reemplazado por el valor del parámetro
     * @V("texto") asocia el parámetro "texto" en el prompt
     * 
     * Retorna tipo Record -> AiServices maneja la conversión automáticamente
     */
    @UserMessage("Extrae la información de: {{texto}}")
    Persona extraerPersona(@V("texto") String texto);
}

public class ChatStructuredOutputMain {
    static void main() {
        // Paso 1: Obtener modelo
        var model = ChatMain.chatModel();

        // Paso 2: Crear extractor con AiServices
        // AiServices genera implementación que:
        // - Inyecta el UserMessage
        // - Inyecta los parámetros
        // - Pide al modelo que retorne JSON
        // - Parsea JSON a Persona
        Extractor extractor = AiServices.builder(Extractor.class)
                .chatModel(model)  // Usando método estándar (no chatModel())
                .build();

        // Paso 3: Usar el extractor
        System.out.println("=== EXTRACTOR DE DATOS ===");
        System.out.println("Entrada: \"Soy María, tengo 30 años y vivo en Quito\"");
        System.out.println();

        // Texto con información de persona
        Persona p = extractor.extraerPersona("Soy María, tengo 30 años y vivo en Quito");

        // Paso 4: Usar el objeto estructurado
        System.out.println("Salida estructurada:");
        System.out.println(p);
        System.out.println();
        System.out.println("Acceso a campos:");
        System.out.println("  Nombre: " + p.nombre());
        System.out.println("  Edad: " + p.edad());
        System.out.println("  Ciudad: " + p.ciudad());
        
        // SALIDA ESPERADA:
        // === EXTRACTOR DE DATOS ===
        // Entrada: "Soy María, tengo 30 años y vivo en Quito"
        // 
        // Salida estructurada:
        // Persona[nombre=María, edad=30, ciudad=Quito]
        // 
        // Acceso a campos:
        //   Nombre: María
        //   Edad: 30
        //   Ciudad: Quito
        
        // CASOS DE USO PRÁCTICOS:
        // - Parseador de CVs: Record con campos tipo, experiencia, skills
        // - Clasificador: Record con category, confidence, reasoning
        // - Extractor de facturas: Record con número, fecha, total, vendedor
        // - Sentimiento: Record con score, positivo, negativo, neutral
    }
}
