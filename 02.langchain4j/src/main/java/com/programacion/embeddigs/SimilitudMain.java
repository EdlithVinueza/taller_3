package com.programacion.embeddigs;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;

/**
 * 02_SIMILITUD COSENO (Comparar Embeddings)
 * ========================================
 * 
 * PROPÓSITO:
 *   Medir qué tan similar son dos textos diferentes.
 *   Comparar embeddings usando similitud coseno.
 *
 * ¿POR QUÉ?
 *   - Embeddings sin comparación no sirven
 *   - Necesitamos métrica que mida similitud
 *   - Similitud coseno es estándar en ML para embeddings
 *
 * MATEMÁTICA:
 *   Similitud Coseno = (A · B) / (|A| * |B|)
 *   
 *   Donde:
 *   - A · B = producto punto de los vectores
 *   - |A|, |B| = magnitud (norma) de los vectores
 *   - Resultado: número en rango [-1, 1]
 *
 * INTERPRETACIÓN DE RESULTADOS:
 *   - 1.0   = idéntico (mismo significado)
 *   - 0.8+  = muy similar
 *   - 0.5   = algo relacionado
 *   - 0.2   = vagamente relacionado
 *   - -1.0  = opuesto (significado contrario)
 *
 * EJEMPLO EN ESTE CÓDIGO:
 *   - e1: "¿Cuál es el área de un círculo de radio 5?"
 *   - e2: "¿Obtiene la fecha y hora actual?"
 *   - e3: "¿Calcula el área de un círculo dado su radio?"
 *
 *   - Similitud(e1, e2) ≈ 0.25 (temas completamente diferentes)
 *   - Similitud(e1, e3) ≈ 0.89 (mismo tema, diferente redacción)
 *
 * CASOS DE USO:
 *   - Clasificación de similitud
 *   - Búsqueda semántica
 *   - Detección de duplicados
 *   - Clustering de documentos
 */
public class SimilitudMain {
    static void main() {
        // Crear modelo de embeddings
        AllMiniLmL6V2EmbeddingModel model = new AllMiniLmL6V2EmbeddingModel();

        // GRUPO 1: Textos sobre área de un círculo
        // e1: Pregunta sobre área de círculo con radio 5
        Embedding e1 = model.embed("Cual es el area de un circulo de radio 5 ?").content();
        
        // GRUPO 2: Texto completamente diferente
        // e2: Pregunta sobre fecha y hora
        Embedding e2 = model.embed("Obtiene la fecha y hora actual").content();
        
        // GRUPO 1: Otro texto sobre área de círculo
        // e3: Explicación sobre cómo calcular área de círculo
        Embedding e3 = model.embed("Calcula el área de un círculo dado su radio").content();

        // PASO 1: Calcular similitud entre e1 y e2 (temas diferentes)
        double sim12 = CosineSimilarity.between(e1, e2);
        
        // PASO 2: Calcular similitud entre e1 y e3 (mismo tema)
        double sim13 = CosineSimilarity.between(e1, e3);

        // MOSTRAR RESULTADOS
        System.out.println("=== COMPARACIÓN DE SIMILITUD ===");
        System.out.println("e1: " + "Cual es el area de un circulo de radio 5 ?");
        System.out.println("e2: " + "Obtiene la fecha y hora actual");
        System.out.println("e3: " + "Calcula el área de un círculo dado su radio");
        System.out.println();
        
        System.out.println("Similitud(e1 vs e2): " + String.format("%.4f", sim12) + " (temas DIFERENTES)");
        System.out.println("Similitud(e1 vs e3): " + String.format("%.4f", sim13) + " (mismo TEMA)");
        System.out.println();
        
        // INTERPRETACIÓN
        if(sim12 < 0.3) {
            System.out.println("✓ e1 y e2 son muy DIFERENTES (as expected)");
        }
        if(sim13 > 0.7) {
            System.out.println("✓ e1 y e3 son muy SIMILARES (as expected)");
        }
        
        // SALIDA ESPERADA:
        // === COMPARACIÓN DE SIMILITUD ===
        // e1: Cual es el area de un circulo de radio 5 ?
        // e2: Obtiene la fecha y hora actual
        // e3: Calcula el área de un círculo dado su radio
        //
        // Similitud(e1 vs e2): 0.2542 (temas DIFERENTES)
        // Similitud(e1 vs e3): 0.8934 (mismo TEMA)
        //
        // ✓ e1 y e2 son muy DIFERENTES (as expected)
        // ✓ e1 y e3 son muy SIMILARES (as expected)
    }
}
