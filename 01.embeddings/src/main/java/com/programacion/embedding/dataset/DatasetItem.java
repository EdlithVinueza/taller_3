package com.programacion.embedding.dataset;

import java.util.List;

/**
 * ESTRUCTURA DE DATO: Pareja Input-Target (Dataset Item)
 * ====================================================
 * 
 * PROPÓSITO:
 *   Contenedor para una pareja de entrada/salida para entrenamiento.
 *   Input: ventana de tokens actuales
 *   Target: ventana de tokens SIGUIENTES (desplazada 1 posición)
 *
 * ¿POR QUÉ?
 *   - Entrenamiento supervisado: necesitamos (entrada, salida esperada)
 *   - Tarea: predecir siguiente token
 *   - Input: [t1, t2, t3, t4]
 *   - Target: [t2, t3, t4, t5] (corrida 1 paso adelante)
 *
 * CONCEPTO DE VENTANA DESLIZANTE:
 *   Secuencia: [1, 2, 3, 4, 5, 6, 7, 8]
 *   
 *   Ventana 1: input=[1,2,3,4], target=[2,3,4,5]
 *   Ventana 2: input=[2,3,4,5], target=[3,4,5,6]
 *   Ventana 3: input=[3,4,5,6], target=[4,5,6,7]
 *   ...
 *   
 *   Modelo aprende: "si ves [1,2,3,4], predice [2,3,4,5]"
 *
 * EJEMPLO CON TOKENS REALES:
 *   Secuencia de tokens: [45, 120, 89, 300, 12, 456, 78, 234]
 *   
 *   DatasetItem(
 *     input=  [45, 120, 89, 300],      // lo que el modelo ve
 *     target= [120, 89, 300, 12]       // lo que debe predecir
 *   )
 *   
 *   Significado: "si ves tokens [45, 120, 89, 300],
 *                 el siguiente token probablemente sea [120, 89, 300, 12]"
 *   
 * USADO POR:
 *   - DataSampling.java: genera muchos DatasetItem
 *   - EmbeddingTest.java: procesa DatasetItem con embeddings
 */
public record DatasetItem(List<Integer> input, List<Integer> target) {
    
    /**
     * Información útil:
     * - ambas listas tienen el MISMO tamaño (context size)
     * - target es input desplazado 1 posición
     * - Se usa para entrenamiento de language models
     */
}
