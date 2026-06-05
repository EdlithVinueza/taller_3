package com.programacion.embedding.tokenization;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * TOKENIZADOR SIMPLE V2 (Versión Mejorada con Token Desconocido)
 * ==============================================================
 * 
 * PROPÓSITO:
 *   Versión mejorada de SimpleTokenizerV1 que maneja palabras fuera de vocabulario.
 *   Las palabras desconocidas se mapean a un token especial <|unk|>.
 *
 * ¿POR QUÉ?
 *   - V1 pierde información al descartar palabras desconocidas
 *   - V2 preserva información usando token especial <|unk|>
 *   - Los modelos reales usan este patrón
 *
 * MEJORAS SOBRE V1:
 *   V1: "hello xyz" → [0] (xyz se descarta)
 *   V2: "hello xyz" → [0, 100] (xyz → 100 es ID de <|unk|>)
 *
 * TOKEN ESPECIAL <|unk|>:
 *   - Reservado para palabras fuera de vocabulario
 *   - Asignado automáticamente en encode()
 *   - Decodificado como "UNK" si algo falla
 *
 * TOKENS ESPECIALES COMUNES:
 *   <|endoftext|> → Marca fin de documento
 *   <|unk|>       → Palabra desconocida
 *   <|pad|>       → Relleno (padding)
 *   <|cls|>       → Inicio de clasificación
 */
public class SimpleTokenizerV2 {
    // Mismo regex que V1 para consistencia
    public static final String regex = "(?=[,.:;?_!\\\"()']|--|\\s)|(?<=[,.:;?_!\\\"()']|--|\\s)";

    // Mapa token → id
    private Map<String, Integer> strToInt;
    // Mapa id → token
    private Map<Integer, String> intToStr;

    /**
     * Constructor: inicializa mapas incluido <|unk|>
     * 
     * @param vocab List<Pair> DEBE incluir Pair con token "<|unk|>"
     */
    public SimpleTokenizerV2(List<Pair> vocab) {
        // Construye ambos índices del vocabulario
        strToInt = vocab.stream()
                .collect(Collectors.toMap(Pair::token, Pair::tokenId));

        intToStr = vocab.stream()
                .collect(Collectors.toMap(Pair::tokenId, Pair::token));
    }

    /**
     * ENCODE: Texto → Lista de IDs (con manejo de desconocidos)
     * 
     * DIFERENCIA CON V1:
     * V1 usa: filter(Objects::nonNull)     → descarta desconocidos
     * V2 usa: getOrDefault(..., <|unk|>)  → mapea a token desconocido
     * 
     * @param text texto a codificar
     * @return lista de IDs (incluye ID de <|unk|> para palabras desconocidas)
     */
    public List<Integer> encode(String text) {
        // Si el token NO existe, lo mapea al ID de <|unk|>
        return Arrays.stream(text.split(regex))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                // CLAVE: getOrDefault mapea desconocidos a <|unk|>
                .map(tokenId -> strToInt.getOrDefault(tokenId, strToInt.get("<|unk|>")))
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * DECODE: Lista de IDs → Texto
     * 
     * Mismo que V1: reconstruye con espacios
     * 
     * @param ids lista de IDs
     * @return texto reconstruido
     */
    public String decode(List<Integer> ids) {
        // Decodifica ids y usa "UNK" si no encuentra el id
        return ids.stream()
                .map(id -> intToStr.getOrDefault(id, "UNK"))
                .collect(Collectors.joining(" "));
    }
    
    /**
     * EJEMPLO DE USO:
     * 
     * Vocabulario con token especial:
     *   Pair(0, "the")
     *   Pair(1, "cat")
     *   Pair(100, "<|unk|>")
     * 
     * encode("the cat"):     → [0, 1]
     * encode("the xyz"):     → [0, 100] (xyz → <|unk|>)
     * encode("abc xyz"):     → [100, 100]
     * decode([0, 1, 100]):   → "the cat UNK"
     */
}
