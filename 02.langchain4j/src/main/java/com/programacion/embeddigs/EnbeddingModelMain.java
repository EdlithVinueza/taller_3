package com.programacion.embeddigs;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.onnx.OnnxEmbeddingModel;
import dev.langchain4j.model.embedding.onnx.PoolingMode;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.output.Response;

import java.nio.file.Paths;

/**
 * 01_FUNDAMENTOS: GENERACIÓN DE EMBEDDINGS
 * ==========================================
 * 
 * PROPÓSITO:
 *   Convertir texto en un vector numérico (embedding) que representa su significado.
 *   Un embedding es la base para búsqueda semántica, clasificación y similitud.
 *
 * ¿POR QUÉ EMBEDDINGS?
 *   - Los modelos de IA necesitan números, no texto
 *   - Dos textos con significado similar tienen embeddings similares
 *   - Permite medir similitud, hacer búsquedas, agrupar documentos
 *
 * PROCESO:
 *   1. Crear modelo de embeddings AllMiniLmL6V2 (384 dimensiones)
 *   2. Pasar texto al modelo
 *   3. Obtener vector de 384 números
 *   4. Mostrar dimensión y valores del vector
 *
 * SALIDA:
 *   Dimension: 384
 *   [-0.123, 0.456, -0.789, ... 384 valores numéricos]
 *
 * NOTA IMPORTANTE:
 *   - AllMiniLmL6V2 es eficiente: rápido y pequeño (~27MB)
 *   - Ideal para ejecutar localmente sin GPU
 *   - Genera vectores de 384 dimensiones
 */
public class EnbeddingModelMain {
    static void main() {
        // OPCIÓN 1: Usar modelo ONNX personalizado desde archivo
        // (Descomentar si tienes archivos model.onnx y tokenizer.json locales)
        //var pathToModel = Paths.get("c:/tools/onnx/model.onnx");
        //var pathToTokenizer = Paths.get("c:/tools/onnx/tokenizer.json");
        //OnnxEmbeddingModel model = new OnnxEmbeddingModel(pathToModel, pathToTokenizer, PoolingMode.MEAN);

        // OPCIÓN 2: Usar modelo preconfigurado AllMiniLmL6V2 (RECOMENDADO)
        // - Se descarga automáticamente la primera vez (~27MB)
        // - Genera embeddings de 384 dimensiones
        AllMiniLmL6V2EmbeddingModel model = new AllMiniLmL6V2EmbeddingModel();
        
        // Generar embedding para un texto en español
        // Response<Embedding> envuelve el resultado y permite acceso al contenido
        Response<Embedding> response = model.embed("Hola, cómo estás?");

        // Extraer el embedding del objeto Response
        Embedding embedding = response.content();

        // SALIDA: mostrar dimensión y valores del vector
        // Dimension = número de componentes del vector (384)
        System.out.println("Dimension: " + embedding.vector().length);
        
        // vectorAsList() = lista de 384 números en rango [-1, 1]
        // Cada número representa una característica semántica del texto
        System.out.println(embedding.vectorAsList());
    }
}
