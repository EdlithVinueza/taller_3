
package com.programacion.embedding.embedding;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.IntArrayList;
import com.knuddels.jtokkit.api.ModelType;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.output.Response;
import com.programacion.embedding.dataset.DatasetItem;

import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 04_EMBEDDINGS: De IDs a Vectores Densos (Significado Semántico)
 * ==============================================================
 *
 * PROPÓSITO:
 *   Demostrar cómo convertir IDs numéricos a vectores densos (embeddings).
 *   Mostrar tanto enfoque manual como modelo real.
 *
 * ¿POR QUÉ EMBEDDINGS?
 *   - IDs son números discretos sin significado semántico
 *   - Embedding = representación vectorial del SIGNIFICADO
 *   - Dos palabras similares tienen embeddings similares
 *   - Los modelos aprenden relaciones entre palabras a través de embeddings
 *
 * DIFERENCIA IDs vs EMBEDDINGS:
 *   ID: 42
 *   → Es solo un número, sin significado intrinseco
 *
 *   Embedding: [-0.12, 0.45, -0.78, 0.01, ...]
 *   → Vector de 384 (o más) dimensiones
 *   → Cada dimensión representa característica semántica
 *   → Captures significado, contexto, relaciones
 *
 * ESTRUCTURA DEL PROGRAMA:
 *   PARTE 1: Crear dataset con ventanas deslizantes
 *   PARTE 2: Embedding MANUAL con matriz de pesos (educativo, DJL)
 *   PARTE 3: Embedding REAL con modelo preentrenado (LangChain4j)
 *
 * SALIDA:
 *   Embedding manual: matrices de números
 *   Embedding real: vector de 384 dimensiones con valores normalizados
 */

public class EmbeddingTest {

    // Mismo corpus para mantener continuidad con tokenizacion y muestreo
    public static final String PATH = "01.embeddings/the-verdict.txt";

    public static void main(String[] args) throws Exception {
        System.out.println("=== PIPELINE COMPLETO: TOKENIZACIÓN → DATASET → EMBEDDINGS ===\n");

        // ========== PARTE 1: GENERAR DATASET ==========
        System.out.println("PARTE 1: Tokenización y muestreo\n");

        // 1) Carga texto
        String raw_text = Files.lines(Paths.get(PATH))
                .reduce(String::concat)
                .orElse("");

        // 2) Tokeniza texto con jtokkit
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding tokenizer = registry.getEncodingForModel(ModelType.TEXT_DAVINCI_003);

        var enc_text = tokenizer.encode(raw_text);
        var enc_text_boxed = enc_text.boxed();
        var enc_sample = enc_text_boxed.subList(50, enc_text_boxed.size());

        System.out.println("Total de tokens: " + enc_text.size());
        System.out.println("Muestra usada: " + enc_sample.size() + " tokens\n");

        int contextSize = 4;

        // 3) Demuestra una pareja
        var x = enc_sample.subList(0, 4);
        var y = enc_sample.subList(1, contextSize + 1);

        System.out.println("Ejemplo input:  " + x);
        System.out.println("Ejemplo target: " + y);

        IntArrayList inputTokens = new IntArrayList();
        x.forEach(inputTokens::add);

        IntArrayList targetTokens = new IntArrayList();
        y.forEach(targetTokens::add);

        // 4) Construye dataset de entrenamiento con ventanas deslizantes
        List<DatasetItem> dataset = new ArrayList<>();

        List<Integer> tokensIds = tokenizer.encode(raw_text).boxed();
        int maxLength = 4;

        IntStream.range(0, tokensIds.size() - maxLength)
                .forEach(i -> {
                    var inputChunk = tokensIds.subList(i, i + maxLength);
                    // Target corrido 1 paso para la tarea de "siguiente token"
                    var targetChunk = tokensIds.subList(i + 1, i + maxLength + 1);

                    dataset.add(new DatasetItem(inputChunk, targetChunk));
                });

        System.out.println("Dataset creado: " + dataset.size() + " items\n");

        // ========== PARTE 2: EMBEDDING MANUAL (Educativo) ==========
        System.out.println("PARTE 2: Embedding manual con DJL\n");
        System.out.println("(Simulación educativa de capa de embeddings)\n");

        // 5) Simula una capa de embedding "a mano" con una matriz de pesos
        // vocabSize = filas (un vector por token)
        // embeddingDim = dimensión del embedding (columnas)
        int vocabSize = 50257;  // tamaño vocabulario GPT
        int embeddingDim = 4;   // pequeño para visualizar

        try (NDManager manager = NDManager.newBaseManager()) {
            // Inicializa la matriz de embeddings con valores aleatorios
            NDArray weights = manager.randomUniform(-1.0f, 1.0f, new Shape(vocabSize, embeddingDim));

            System.out.println("Matriz de embeddings creada: " + weights.getShape());
            System.out.println("(50257 tokens × 4 dimensiones)\n");

            // Para primeros 2 items del dataset
            dataset.stream().limit(2).forEach(item -> {

                // Convierte IDs a long para indexar NDArray
                var input = item.input().stream()
                        .mapToLong(Integer::longValue)
                        .toArray();

                // Obtiene los vectores embedding de los tokens de entrada
                // Esto es como: "para cada token ID, obtén su vector de 4 dimensiones"
                NDArray indices = manager.create(input);
                var embedding = weights.get(indices);

                System.out.println("Input indices: " + Arrays.toString(input));
                System.out.println("Embedding shape: " + embedding.getShape());
                System.out.println("Embedding:\n" + embedding);
                System.out.println();

            });
        }

        // ========== PARTE 3: EMBEDDING REAL (LangChain4j) ==========
        System.out.println("PARTE 3: Embedding real con modelo preentrenado\n");
        System.out.println("(AllMiniLmL6V2: modelo ONNX de 384 dimensiones)\n");

        // 6) Embedding con un modelo real listo para usar (LangChain4j + ONNX)
        EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();
        var text = "hello world";

        System.out.println("Texto: \"" + text + "\"\n");

        // La respuesta trae el vector numérico del texto
        Response<Embedding> response = embeddingModel.embed(text);

        float[] vector = response.content().vector();

        System.out.println("Embedding size: " + vector.length + " dimensiones");
        System.out.println("Primeros 10 valores: " + Arrays.toString(
                java.util.Arrays.copyOf(vector, Math.min(10, vector.length))
        ));
        System.out.println("...");
        System.out.println("Últimos 10 valores: " + Arrays.toString(
                java.util.Arrays.copyOfRange(vector, Math.max(0, vector.length - 10), vector.length)
        ));

        // COMPARACIÓN: Manual vs Real
        System.out.println("\n=== COMPARACIÓN ===");
        System.out.println("Manual (DJL):     4 dimensiones,  números aleatorios [-1, 1]");
        System.out.println("Real (LangChain): 384 dimensiones, representa SIGNIFICADO");
        System.out.println("\nModelo real fue entrenado con millones de textos.");
        System.out.println("Cada dimensión captura aspecto semántico diferente.");
    }
}