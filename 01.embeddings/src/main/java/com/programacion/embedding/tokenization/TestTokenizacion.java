package com.programacion.embedding.tokenization;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 01_DEMOSTRACIÓN COMPLETA: Tokenización Paso a Paso
 * =================================================
 * 
 * PROPÓSITO:
 *   Ejecutable que demuestra tokenización completa:
 *   - Lectura de corpus
 *   - Construcción de vocabulario
 *   - Codificación/decodificación con V1 y V2
 *
 * ¿POR QUÉ?
 *   - Integra todo lo aprendido en Pair, V1, V2
 *   - Demuestra pipeline real: archivo → vocab → tokenización
 *   - Muestra diferencia entre V1 (pierde datos) y V2 (preserva)
 *
 * PROCESO:
 *   1. vocabulary(file): construye vocabulario SIN tokens especiales
 *   2. vocabularyEx(file): construye vocabulario CON tokens especiales
 *   3. Demo V1: muestra tokenización básica
 *   4. Demo V2: muestra manejo de desconocidos
 *
 * ARCHIVOS:
 *   - Entrada: 01.embeddings/the-verdict.txt
 *   - Salida: Imprime a consola
 *
 * SALIDA ESPERADA:
 *   [0] Pair[--: 0]
 *   [1] Pair["": 1]
 *   ...
 *   [50] Pair[you: 50]
 *   
 *   [123, 456, 789, ...]
 *   DECODER: Hello world ...
 */
public class TestTokenizacion {
    // Ruta al corpus de ejemplo
    public static final String PATH = "01.embeddings/the-verdict.txt";

    /**
     * Construye vocabulario SIMPLE (sin tokens especiales)
     * 
     * PASOS:
     * 1. Lee archivo completo
     * 2. Tokeniza con regex
     * 3. Limpia tokens vacíos
     * 4. Obtiene únicos y ordenados
     * 5. Asigna IDs secuenciales
     * 
     * @param fileName ruta al corpus
     * @return List<Pair> vocabulario con ID de 0 en adelante
     */
    public static List<Pair> vocabulary(String fileName) throws Exception {

        // PASO 1: Carga texto crudo desde archivo
        String raw_text = Files.lines(Paths.get(fileName))
                .reduce(String::concat)  // concatena todas las líneas
                .orElse("");

        // PASO 2: Regex que separa palabras, signos y espacios de forma consistente
        String regex = "(?=[,.:;?_!\\\"()']|--|\\s)|(?<=[,.:;?_!\\\"()']|--|\\s)";

        // PASO 3: Tokeniza texto (divide según regex)
        var tokens = raw_text.split(regex);

        // PASO 4: Limpia tokens vacíos
        var preprocessed = Stream.of(tokens)
                .map(String::trim)                    // elimina espacios
                .filter(s -> !s.isEmpty())            // descarta vacíos
                .toList();

        // PASO 5: Obtiene vocabulario único y ordenado alfabéticamente
        var allWords = preprocessed.stream()
                .distinct()                           // elimina duplicados
                .sorted()                             // ordena alfabéticamente
                .toList();

        // PASO 6: Asigna IDs secuenciales a cada token del vocabulario
        // Contador atomico para thread-safety (no necesario aquí, pero es buena práctica)
        AtomicInteger counter = new AtomicInteger(0);
        var vocab = allWords.stream()
                .map(it -> new Pair(counter.getAndIncrement(), it))
                .toList();
        return vocab;
    }

    /**
     * Construye vocabulario EXTENDIDO (con tokens especiales)
     * 
     * Similar a vocabulary() pero AGREGA tokens especiales:
     * - <|endoftext|>: marca fin de documento
     * - <|unk|>: palabra desconocida
     * 
     * @param fileName ruta al corpus
     * @return List<Pair> vocabulario con tokens especiales
     */
    public static List<Pair> vocabularyEx(String fileName) throws Exception {

        // Repite construcción de vocabulario básico...
        String raw_text = Files.lines(Paths.get(fileName))
                .reduce(String::concat)
                .orElse("");

        String regex = "(?=[,.:;?_!\\\"()']|--|\\s)|(?<=[,.:;?_!\\\"()']|--|\\s)";

        var tokens = raw_text.split(regex);

        var preprocessed = Stream.of(tokens)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        // Obtiene palabras únicas
        var allWords = preprocessed.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());  // toList() mutable para agregar

        // ...y agrega tokens especiales usados por modelos reales
        // Estos son reservados y NO aparecen en el corpus original
        allWords.add("<|endoftext|>");  // marca fin de secuencia
        allWords.add("<|unk|>");        // palabra desconocida

        // Vuelve a asignar IDs con los nuevos tokens especiales
        AtomicInteger counter = new AtomicInteger(0);
        var vocab = allWords.stream()
                .map(it -> new Pair(counter.getAndIncrement(), it))
                .toList();
        return vocab;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== TOKENIZACIÓN V1 (SIN MANEJO DE DESCONOCIDOS) ===\n");

        // DEMO 1: Tokenización básica con vocabulario sin tokens especiales
        var vocab = vocabulary(PATH);
        
        // Muestra primeros 51 tokens del vocabulario
        vocab.stream()
                .takeWhile(it -> it.tokenId() < 51)
                .forEach(System.out::println);

        System.out.println("\n--- Codificación/Decodificación V1 ---");
        
        // Frase de prueba
        var text = "\"It's the last he painted, you know,\" Mrs. Gisburn said with pardonable pride:";

        // Codifica y decodifica para ver el ciclo completo: texto → ids → texto
        SimpleTokenizerV1 tokenizer = new SimpleTokenizerV1(vocab);

        var ids = tokenizer.encode(text);

        System.out.println("Texto original: " + text);
        System.out.println("IDs: " + ids);
        System.out.println("Decodificado: " + tokenizer.decode(ids));

        // DEMO 2: Tokenización con vocabulario extendido (<|endoftext|>, <|unk|>)
        System.out.println("\n=== TOKENIZACIÓN V2 (CON MANEJO DE DESCONOCIDOS) ===\n");
        
        var vocabEx = vocabularyEx(PATH);

        // Muestra últimos 5 tokens (incluye los especiales)
        System.out.println("Últimos 5 tokens (incluye especiales):");
        vocabEx.stream()
                .skip(vocabEx.size() - 5)
                .forEach(System.out::println);

        System.out.println("\n--- Codificación/Decodificación V2 ---");
        
        // Crea texto con token especial y palabras potencialmente desconocidas
        var text1 = "Hello, do you like tea?";
        var text2 = "In the sunlit terraces of the palace.";
        text = text1 + " <|endoftext|> " + text2;

        SimpleTokenizerV2 tokenizer2 = new SimpleTokenizerV2(vocabEx);

        var ids2 = tokenizer2.encode(text);

        System.out.println("Texto: " + text);
        System.out.println("IDs: " + ids2);
        System.out.println("Decodificado: " + tokenizer2.decode(ids2));
        
        // OBSERVACIÓN:
        // - Si hay palabras fuera del vocabulario original
        // - V1 las descarta (pérdida de información)
        // - V2 las mapea a <|unk|> (se preserva la estructura)
    }
}
