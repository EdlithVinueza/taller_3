package com.programacion.embedding.tokenization;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * TOKENIZADOR SIMPLE V1 (Versión Didáctica Básica)
 * ===============================================
 * 
 * PROPÓSITO:
 *   Convertir texto en IDs y viceversa.
 *   Versión educativa que ignora tokens desconocidos.
 *
 * ¿POR QUÉ?
 *   - Los modelos necesitan números para procesar
 *   - Tokenización = dividir texto en palabras/símbolos + asignar IDs
 *   - V1 es simple: si palabra no está en vocabulario, se ignora
 *
 * PROCESO:
 *   encode():  "Hello world" → split → lookup → [ID1, ID2, ...]
 *   decode():  [ID1, ID2, ...] → lookup inverso → "hello world"
 *
 * LIMITACIÓN EN V1:
 *   - Si una palabra NO está en vocabulario, se descarta
 *   - Pérdida de información
 *   - Solución: usar V2 con token <|unk|>
 *
 * EJEMPLO:
 *   Vocabulario: {0: "hello", 1: "world", 2: "!"}
 *   
 *   encode("hello world"):  → [0, 1]
 *   encode("hello xyz"):    → [0] (xyz se descarta, no está en vocab)
 *   
 *   decode([0, 1]):         → "hello world"
 */
public class SimpleTokenizerV1 {
    // Regex que separa: comas, puntos, espacios, comillas, etc.
    public static final String regex = "(?=[,.:;?_!\\\"()']|--|\\s)|(?<=[,.:;?_!\\\"()']|--|\\s)";

    // Mapa rápido: token (string) → ID (int)
    // Ejemplo: "hello" → 0, "world" → 1
    private Map<String, Integer> strToInt;
    
    // Mapa inverso: ID (int) → token (string)
    // Ejemplo: 0 → "hello", 1 → "world"
    // Usado para decodificar IDs de vuelta a texto
    private Map<Integer, String> intToStr;

    /**
     * Constructor: inicializa mapas de vocabulario
     * 
     * @param vocab List<Pair> con todos los tokens y sus IDs
     */
    public SimpleTokenizerV1(List<Pair> vocab) {
        // Construye mapa token → id para codificar rápido
        // {"the" → 1, "cat" → 5, "sat" → 10, ...}
        strToInt = vocab.stream()
                .collect(Collectors.toMap(Pair::token, Pair::tokenId));

        // Construye mapa inverso id → token para decodificar
        // {1 → "the", 5 → "cat", 10 → "sat", ...}
        intToStr = vocab.stream()
                .collect(Collectors.toMap(Pair::tokenId, Pair::token));
    }

    /**
     * ENCODE: Texto → Lista de IDs
     * 
     * Pasos:
     * 1. Split con regex
     * 2. Trim (eliminar espacios)
     * 3. Filter vacíos
     * 4. Lookup en strToInt
     * 5. Filter null (elimina desconocidos)
     * 
     * @param text texto a convertir a IDs
     * @return lista de IDs (descarta palabras fuera de vocab)
     */
    public List<Integer> encode(String text) {
        // Flujo de codificación:
        // texto → split → limpieza → lookup → elimina desconocidos
        return Arrays.stream(text.split(regex))
                .map(String::trim)                      // elimina espacios
                .filter(s -> !s.isEmpty())              // ignora vacíos
                .map(strToInt::get)                     // busca en mapa
                .filter(Objects::nonNull)               // elimina null (desconocidos)
                .toList();
    }

    /**
     * DECODE: Lista de IDs → Texto
     * 
     * Pasos:
     * 1. Para cada ID
     * 2. Lookup inverso en intToStr
     * 3. Si no existe, usa "UNK" como placeholder
     * 4. Join con espacios
     * 
     * @param ids lista de IDs a convertir a texto
     * @return texto reconstruido
     */
    public String decode(List<Integer> ids) {
        // Reconstruye tokens en texto separando por espacio
        // Si un id no existe (error), usa "UNK" como marcador
        return ids.stream()
                .map(id -> intToStr.getOrDefault(id, "UNK"))
                .collect(Collectors.joining(" "));
    }
}
