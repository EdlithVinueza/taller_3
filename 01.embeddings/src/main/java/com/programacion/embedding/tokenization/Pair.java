package com.programacion.embedding.tokenization;

/**
 * ESTRUCTURA DE DATOS: Par Token-ID
 * ================================
 * 
 * PROPÓSITO:
 *   Contenedor simple que relaciona cada token textual (palabra, símbolo) 
 *   con su identificador numérico único.
 *
 * ¿POR QUÉ?
 *   - Los modelos de IA trabajan con números, no texto
 *   - Cada palabra única necesita un ID único
 *   - Pair permite mapeo bidireccional: palabra ↔ ID
 *
 * ESTRUCTURA:
 *   record Pair(int tokenId, String token)
 *   
 *   Ejemplo:
 *   - Pair(0, "<|endoftext|>") → token especial de fin
 *   - Pair(1, "the") → artículo más común
 *   - Pair(42, "hello") → palabra ordinaria
 *   - Pair(100, "<|unk|>") → token desconocido
 *
 * ¿CÓMO SE TRATA?
 *   - Vocabulario = List<Pair>
 *   - Cada Pair es una entrada en el diccionario del modelo
 *   - SimpleTokenizerV1/V2 usan Pair para crear mapas
 *
 * EJEMPLO DE SALIDA:
 *   Pair[the: 1]
 *   Pair[hello: 42]
 *   Pair[<|unk|>: 100]
 */
public record Pair(int tokenId, String token) {
    @Override
    public String toString() {
        return "Pair[" + token + ": " + tokenId + "]";
    }
}
