package com.programacion.embedding.dataset;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.IntArrayList;
import com.knuddels.jtokkit.api.ModelType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 03_MUESTREO Y CREACIÓN DE DATASET (Ventanas Deslizantes)
 * ========================================================
 * 
 * PROPÓSITO:
 *   Generar un dataset completo de pares input/target para entrenar.
 *   Usando ventanas deslizantes sobre la secuencia de tokens.
 *
 * ¿POR QUÉ?
 *   - No podemos entrenar con toda la secuencia a la vez (muy grande)
 *   - Dividimos en ventanas pequeñas (context size)
 *   - Cada ventana genera un DatasetItem
 *   - Modelo aprende a predecir siguiente token basado en ventana
 *
 * CONCEPTO: VENTANA DESLIZANTE (Sliding Window)
 *   Secuencia: [1, 2, 3, 4, 5, 6, 7, 8]
 *   Context Size = 4
 *   
 *   Paso 1: input=[1,2,3,4],  target=[2,3,4,5]
 *   Paso 2: input=[2,3,4,5],  target=[3,4,5,6]
 *   Paso 3: input=[3,4,5,6],  target=[4,5,6,7]
 *   Paso 4: input=[4,5,6,7],  target=[5,6,7,8]
 *   ...
 *   
 *   De una secuencia de N tokens, generamos N-contextSize pares
 *
 * PROCESO:
 *   1. Leer corpus (the-verdict.txt)
 *   2. Tokenizar con JTokkit (real)
 *   3. Crear ventanas deslizantes
 *   4. Para cada ventana: crear DatasetItem(input, target)
 *   5. Guardar en List<DatasetItem>
 *
 * SALIDA:
 *   Dataset de ~1000+ items (depende tamaño de corpus)
 *   Cada item listo para entrenar modelo
 */
public class DataSampling {
    // Corpus de ejemplo para generar pares input/target
    public static final String PATH = "01.embeddings/the-verdict.txt";

    static void main() throws Exception {
        // PASO 1: Lectura del texto
        System.out.println("=== CREACIÓN DE DATASET CON VENTANAS DESLIZANTES ===\n");
        
        String raw_text = Files.lines(Paths.get(PATH))
                .reduce(String::concat)                 // concatena líneas
                .orElse("");

        // PASO 2: Tokenizador real (jtokkit) para obtener IDs de tokens
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding tokenizer = registry.getEncodingForModel(ModelType.TEXT_DAVINCI_003);

        // Tokeniza TODO el corpus
        var enc_text = tokenizer.encode(raw_text);
        
        // Convierte IntArrayList a List<Integer> para manipular más fácil
        var enc_text_boxed = enc_text.boxed();
        
        // Toma muestra del corpus (del token 50 en adelante)
        // Para no sesgar con tokens especiales al inicio
        var enc_sample = enc_text_boxed.subList(50, enc_text_boxed.size());

        System.out.println("Total de tokens en corpus: " + enc_text.size());
        System.out.println("Muestra usada: " + enc_sample.size() + " tokens");

        // PASO 3: Define tamaño de contexto (ventana)
        int contextSize = 4;
        System.out.println("Context size (tamaño de ventana): " + contextSize);

        // PASO 4: Demuestra una pareja input/target
        System.out.println("\n--- Ejemplo de una pareja input/target ---");
        
        var x = enc_sample.subList(0, contextSize);        // primeros 4 tokens
        var y = enc_sample.subList(1, contextSize + 1);    // tokens 2-5 (desplazado)

        System.out.println("Input (x): " + x);
        System.out.println("Target(y): " + y);

        // Convierte a IntArrayList para decodificar
        IntArrayList inputTokens = new IntArrayList();
        x.forEach(inputTokens::add);

        IntArrayList targetTokens = new IntArrayList();
        y.forEach(targetTokens::add);

        // Muestra qué significa esto en texto
        System.out.println("Input decodificado: " + tokenizer.decode(inputTokens));
        System.out.println("Target decodificado: " + tokenizer.decode(targetTokens));
        System.out.println("(Observa cómo target es input desplazado 1 paso)");

        // PASO 5: Construye dataset COMPLETO de ventanas deslizantes
        System.out.println("\n--- Creando dataset completo ---");
        
        List<DatasetItem> dataset = new ArrayList<>();

        List<Integer> tokensIds = tokenizer.encode(raw_text).boxed();
        int maxLength = 4;  // tamaño de contexto

        // Crea ventana deslizante sobre toda la secuencia
        // Para cada posición i desde 0 hasta tamaño_tokens - maxLength:
        IntStream.range(0, tokensIds.size() - maxLength)
                .forEach(i -> {
                    // Ventana actual: desde i hasta i+maxLength
                    var inputChunk = tokensIds.subList(i, i + maxLength);
                    
                    // Target: mismo pero corrido 1 posición (i+1 hasta i+maxLength+1)
                    var targetChunk = tokensIds.subList(i + 1, i + maxLength + 1);

                    // Agrega pareja al dataset
                    dataset.add(new DatasetItem(inputChunk, targetChunk));
                });

        // PASO 6: Verifica cantidad y ejemplos del dataset generado
        System.out.println("Dataset generado: " + dataset.size() + " pares input/target");
        System.out.println("\n--- Primeros 2 items del dataset ---");
        System.out.println("Item 0: " + dataset.get(0));
        System.out.println("Item 1: " + dataset.get(1));
        
        System.out.println("\n=== DATASET LISTO PARA ENTRENAR ===");
        System.out.println("Puedes usar este dataset para:");
        System.out.println("- Entrenar modelo de predicción de siguiente token");
        System.out.println("- Aprender embeddings");
        System.out.println("- Análisis de secuencias");
    }
}

