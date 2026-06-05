package com.programacion.embeddigs;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;

import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.util.List;

/**
 * 03_BÚSQUEDA SEMÁNTICA (Búsqueda Manual)
 * =======================================
 * 
 * PROPÓSITO:
 *   Buscar en una lista de documentos cuál es el más similar a una consulta.
 *   Implementar búsqueda semántica sin usar almacén de base de datos.
 *
 * ¿POR QUÉ?
 *   - Búsqueda por palabras clave NO funciona bien
 *   - "¿Qué es programación?" no encontraría "Java es un lenguaje OOP"
 *   - Con embeddings: comparamos SIGNIFICADO, no palabras exactas
 *
 * PROCESO:
 *   1. Tener lista de documentos candidatos
 *   2. Generar embedding para cada documento
 *   3. Generar embedding para la consulta del usuario
 *   4. Calcular similitud coseno entre consulta y cada documento
 *   5. Retornar documento con mayor similitud
 *
 * ALGORITMO:
 *   - Similitud coseno = producto punto de dos vectores / (norma1 * norma2)
 *   - Valor en rango [-1, 1]:
 *     * 1.0 = idéntico
 *     * 0.5 = algo similar
 *     * 0.0 = no relacionados
 *     * -1.0 = opuesto
 *
 * LIMITACIONES:
 *   - Genera embedding para CADA búsqueda
 *   - Recorre TODOS los documentos cada vez
 *   - O(n) búsquedas: lento con muchos documentos
 *   - Siguiente paso: usar InMemoryEmbeddingStore para índices
 */
public class BusquedaSemanticaMain {
    static void main() {
        // Documentos sobre diferentes temas
        var documentos = List.of(
                "Java is an object-oriented programming language",        // Tema: OOP
                "Python is popular for artificial intelligence",           // Tema: ML/AI
                "The coffee machine is in the kitchen",                    // Tema: Objetos
                "Embeddings represent text as vectors"                     // Tema: Embeddings
        );
        
        // Crear modelo de embeddings
        var model = new AllMiniLmL6V2EmbeddingModel();

        // PASO 1: Generar embeddings para TODOS los documentos
        // map(model::embed)     -> genera embedding para cada texto
        // map(Response::content) -> extrae el Embedding del Response
        // toList()              -> convierte en lista
        List<Embedding> embeddings = documentos.stream()
                .map(model::embed)
                .map(Response::content)
                .toList();

        // PASO 2: Generar embedding para la CONSULTA
        // Consulta sobre programación orientada a objetos
        String consulta = "object oriented programming";
        Embedding query = model.embed(consulta).content();

        // PASO 3: Encontrar el documento MÁS SIMILAR
        var index = 0;
        int mejorIndice = 0;
        double mejorScore = -1.0;

        // Iterar sobre cada documento
        for(var it : embeddings) {
            // Calcular similitud coseno entre consulta y documento
            // CosineSimilarity retorna valor en [0, 1] para embeddings normalizados
            double score = CosineSimilarity.between(it, query);

            // Mostrar similitud para cada documento
            System.out.printf("Documento %d - Similitud: %.2f%n", index, score);

            // Actualizar mejor resultado si este tiene mayor similitud
            if(score > mejorScore) {
                mejorScore = score;
                mejorIndice = index;
            }
            index++;
        }

        // PASO 4: Mostrar resultado final
        System.out.println("\n--- MEJOR RESULTADO ---");
        System.out.println("Documento: " + documentos.get(mejorIndice));
        System.out.println("Similitud: " + mejorScore);
        
        // SALIDA ESPERADA:
        // Documento 0 - Similitud: 0.92
        // Documento 1 - Similitud: 0.15
        // Documento 2 - Similitud: 0.08
        // Documento 3 - Similitud: 0.45
        // --- MEJOR RESULTADO ---
        // Documento: Java is an object-oriented programming language
        // Similitud: 0.92
    }
}
