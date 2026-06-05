package com.programacion.embeddigs;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.List;

/**
 * 04_ALMACENAMIENTO DE EMBEDDINGS (InMemory Store)
 * ================================================
 * 
 * PROPÓSITO:
 *   Almacenar documentos y embeddings en memoria para búsquedas eficientes.
 *   Implementar Top-K retrieval sin iterar manualmente.
 *
 * ¿POR QUÉ?
 *   - BusquedaSemanticaMain recorre TODOS los documentos (O(n))
 *   - Con muchos documentos (1M+), es muy lento
 *   - InMemoryEmbeddingStore indexa automáticamente
 *   - Búsquedas más rápidas y Top-K integrado
 *
 * VENTAJAS:
 *   - API limpia y simples
 *   - Datos en RAM: muy rápido
 *   - Ideal para prototipado y apps pequeñas
 *   - Escalable a miles de documentos
 *
 * LIMITACIONES:
 *   - Datos se pierden al reiniciar
 *   - No persiste en disco
 *   - Memoria limitada por RAM disponible
 *   - Para producción: usar vector DB (Pinecone, Weaviate, etc.)
 *
 * PROCESO:
 *   1. Crear InMemoryEmbeddingStore vacío
 *   2. Para cada documento:
 *      - Generar embedding
 *      - Guardar con (key, embedding, TextSegment)
 *   3. Crear búsqueda: EmbeddingSearchRequest con:
 *      - queryEmbedding: embedding de consulta
 *      - maxResults: Top-K (cuántos resultados retornar)
 *   4. Ejecutar búsqueda
 *   5. Iterar sobre matches
 *
 * API DE BÚSQUEDA:
 *   - store.search(request): retorna EmbeddingSearchResult
 *   - result.matches(): lista de Match<TextSegment>
 *   - match.score(): similitud (0-1)
 *   - match.embedded().text(): texto original
 */
public class InMemoryEmbeddingStoreMain {

    static void main() {
        // Documentos de prueba
        var documentos = List.of(
                "Java is an object-oriented programming language",
                "Python is popular for artificial intelligence",
                "The coffee machine is in the kitchen",
                "Embeddings represent text as vectors"
        );
        
        // Crear modelo de embeddings
        var model = new AllMiniLmL6V2EmbeddingModel();

        // PASO 1: Crear almacén de embeddings vacío
        // TextSegment = tipo de datos que se almacena junto con el embedding
        InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();

        // PASO 2: Agregar todos los documentos al almacén
        documentos.forEach(it -> {
            // Generar key única para cada documento
            String key = "doc-" + (store.size() + 1);  // "doc-1", "doc-2", etc.
            
            // Generar embedding para el documento
            var embedding = model.embed(it);

            // Agregar al almacén:
            // - key: identificador único
            // - embedding.content(): el vector
            // - TextSegment.from(it): envuelve el texto original
            store.add(key, embedding.content(), TextSegment.from(it));
        });
        
        // PASO 3: Crear consulta de búsqueda
        String consulta = "object oriented programmig";  // Nota: typo intencional para probar tolerancia
        
        // Construir solicitud de búsqueda con:
        // - queryEmbedding: embedding de la consulta
        // - maxResults: retornar top-2 (los 2 más similares)
        var searchQuery = EmbeddingSearchRequest.builder()
                .queryEmbedding(model.embed(consulta).content())
                .maxResults(2)  // Top-K = 2
                .build();

        // PASO 4: Ejecutar búsqueda
        EmbeddingSearchResult<TextSegment> searchResult = store.search(searchQuery);

        // PASO 5: Procesar resultados
        System.out.println("=== RESULTADOS TOP-2 ===");
        for(var match : searchResult.matches()) {
            // Mostrar score de similitud y texto
            System.out.printf("Score: %.2f - %s%n", 
                    match.score(),                    // Similitud (0-1)
                    match.embedded().text()           // Texto original
            );
        }
        
        // SALIDA ESPERADA:
        // === RESULTADOS TOP-2 ===
        // Score: 0.92 - Java is an object-oriented programming language
        // Score: 0.45 - Embeddings represent text as vectors
    }
}
