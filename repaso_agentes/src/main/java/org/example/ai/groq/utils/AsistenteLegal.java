package org.example.ai.groq.utils;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * INTERFAZ DE UTILIDAD: Asistente Legal
 * ====================================
 * 
 * Propósito: Define dos servicios de IA especializados en temas legales/educativos
 * Usada por: ChatAiServiceMain.java
 *
 * MÉTODOS:
 * 1. responder() - Asistente educativo en educación superior
 * 2. consultar() - Asistente legal en leyes de Ecuador (comentado)
 *
 * DECORADORES:
 * - @SystemMessage: rol y contexto global del modelo
 * - @UserMessage: prompt específico por método
 * - @V("variable"): parámetro inyectado en el prompt
 *
 * CÓMO USA ESTO ChatAiServiceMain:
 * var asistente = AiServices.create(AsistenteLegal.class, model);
 * → AiServices genera una clase que implementa esta interfaz
 * → Cada método: inyecta decoradores + llama modelo + retorna resultado
 *
 * EJEMPLO DE EJECUCIÓN:
 * Input:  asistente.responder("¿Qué es un crédito educativo?")
 * ↓
 * AiServices inyecta:
 *   - SystemMessage: "Eres un asistente legal en temas de educación superior"
 *   - UserMessage: "Responde a la pregunta: ¿Qué es un crédito educativo?"
 * ↓
 * Modelo procesa
 * ↓
 * Output: "Un crédito educativo es un financiamiento..."
 */
public interface AsistenteLegal {

    /**
     * MÉTODO 1: responder()
     * 
     * Asistente para preguntas sobre educación superior
     * Sin decoradores específicos aquí, usa los de consultar()
     * 
     * @param pregunta la pregunta sobre educación
     * @return respuesta del asistente
     */
    String responder(String pregunta);

    /**
     * MÉTODO 2: consultar()
     * 
     * Asistente legal (comentado porque usa implementación alternativa)
     * 
     * Versión comentada (tema: leyes de Ecuador):
     * @SystemMessage("Eres un asistente legal experto en leyes de Ecuador")
     * @UserMessage("Responde a la pregunta utilizando argumentos legales: {{pregunta}}")
     * String consultar(@V("pregunta") String pregunta);
     * 
     * VERSIÓN ACTUAL: Educación superior
     */
    @SystemMessage("Eres un asistente legal en temas de educacion superior")
    @UserMessage("""
            Responde a la pregunta: {{pregunta}}
            
            Si no tienes la respuesta usa la seccion de datos que incluye
            
            DATOS
            -------
            Información sobre educación superior en Ecuador:
            - Créditos educativos disponibles
            - Becas del gobierno
            - Leyes de educación superior
            - Instituciones reconocidas
            """)
    String consultar(@V("pregunta") String pregunta);
}
