package com.programacion.embedding.tokenization;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.IntArrayList;
import com.knuddels.jtokkit.api.ModelType;

/**
 * 02_TOKENIZADOR REAL: JTokkit (OpenAI Compatible)
 * ================================================
 * 
 * PROPÓSITO:
 *   Usar tokenizador REAL (JTokkit) compatible con modelos OpenAI.
 *   Demuestra cómo funcionan los tokenizadores en producción.
 *
 * ¿POR QUÉ?
 *   - SimpleTokenizerV1/V2 son educativos, pero simplistas
 *   - Modelos reales usan tokenizadores sofisticados (BPE, SentencePiece, etc.)
 *   - JTokkit es la implementación Java de GPT tokenizers
 *   - Compatible con: GPT-3, GPT-4, Text-Davinci, etc.
 *
 * DIFERENCIA CON V1/V2:
 *   Educativo (V1/V2):
 *   - Split por regex simple
 *   - Vocabulario pequeño (~10K palabras)
 *   - Tokens = palabras enteras
 *   
 *   Real (JTokkit):
 *   - BPE (Byte Pair Encoding)
 *   - Vocabulario grande (50K+ tokens)
 *   - Tokens = subpalabras (ej: "engineering" → "en", "gin", "eer", "ing")
 *
 * PROCESO:
 *   1. Crear registro de encodings
 *   2. Obtener encoding para un modelo específico
 *   3. encodeOrdinary(text) → IntArrayList de tokens
 *   4. decode(ids) → texto reconstruido
 *
 * MODELOS DISPONIBLES:
 *   - ModelType.GPT_3_5_TURBO
 *   - ModelType.GPT_4
 *   - ModelType.TEXT_DAVINCI_003
 *   - ModelType.TEXT_DAVINCI_002
 *   - etc.
 */
public class TokkitTokenizer {
    // Demo usando un tokenizador real (jtokkit) compatible con modelos OpenAI
    static void main() {
        // PASO 1: Registro con encodings predefinidos
        // Registry contiene encodings para todos los modelos OpenAI
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();

        // PASO 2: Selecciona el encoding según el modelo
        // (cada modelo tiene tokenizador ligeramente diferente)
        // Encoding tokenizer = registry.getEncodingForModel(ModelType.GPT_4);
        Encoding tokenizer = registry.getEncodingForModel(ModelType.TEXT_DAVINCI_003);

        // Texto de entrada para observar tokenización y reconstrucción
        var text = "Hello, do you like tea? In the sunlit terraces of someunknownPlace.";
        // var text = "someunknownPlace."; // Esta palabra no está en diccionario → se tokeniza en partes

        // PASO 3: Codifica texto a IDs
        // encodeOrdinary: convierte texto en ids, sin tratar tokens especiales
        // (Otros métodos: encode() incluye tokens especiales)
        IntArrayList ids = tokenizer.encodeOrdinary(text);

        // PASO 4: Muestra información
        System.out.println("=== TOKENIZACIÓN CON JTOKKIT ===");
        System.out.println("Texto original: " + text);
        System.out.println("Tokens: " + ids.size());
        System.out.println("IDs: " + ids);

        // PASO 5: Decodifica de vuelta a texto
        // Recupera una representación textual desde ids
        System.out.println("Decodificado: " + tokenizer.decode(ids));
        
        // OBSERVACIÓN:
        // - Si texto decodificado ≠ texto original → hay pérdida (raro con GPT)
        // - Normalmente el viaje ida-vuelta es lossless (sin pérdida)
        // - Excepción: caracteres especiales o whitespace pueden cambiar
        
        // SALIDA ESPERADA (aproximada):
        // Tokens: 18
        // IDs: [9906, 11, 656, 345, 1398, 87, 30, 763, 4251, 10429, ...]
        // Decodificado: Hello, do you like tea? In the sunlit terraces of someunknownPlace.
    }
}
