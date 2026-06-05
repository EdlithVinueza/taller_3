package org.example.ai.groq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * CONFIGURACIÓN DE LLM DESDE YAML
 * ==============================
 *
 * Carga la configuración de LLM desde src/main/resources/application.yml
 * y secrets.yml para las API keys.
 *
 * Estructura esperada en application.yml:
 *   llm:
 *     groq:
 *       base-url: "..."
 *       model-name: "..."
 *       log-requests: true
 *       log-responses: true
 *
 * Estructura esperada en secrets.yml:
 *   api-keys:
 *     groq: "gsk_..."
 */
public class LlmConfigLoader {

    /**
     * Carga la configuración de Groq desde YAML
     * @return Objeto GroqConfig con toda la configuración
     */
    public static GroqConfig loadGroqConfig() {
        try {
            // Cargar application.yml
            var appConfig = loadYaml("application.yml");

            // Cargar secrets.yml
            var secretsConfig = loadYaml("secrets.yml");

            // Extraer la configuración de Groq
            @SuppressWarnings("unchecked")
            Map<String, Object> llmConfig = (Map<String, Object>) appConfig.get("llm");
            @SuppressWarnings("unchecked")
            Map<String, Object> groqConfig = (Map<String, Object>) llmConfig.get("groq");

            // Extraer la API key de secrets
            @SuppressWarnings("unchecked")
            Map<String, Object> apiKeys = (Map<String, Object>) secretsConfig.get("api-keys");
            String apiKey = (String) apiKeys.get("groq");

            // Si no está en secrets, intentar desde variable de entorno
            if (apiKey == null || apiKey.isEmpty()) {
                apiKey = System.getenv("GROQ_API_KEY");
            }

            // Crear objeto GroqConfig
            @SuppressWarnings("unchecked")
            java.util.List<String> fallbacks = (java.util.List<String>) groqConfig.get("fallback-models");

            return new GroqConfig(
                    (String) groqConfig.get("base-url"),
                    (String) groqConfig.get("model-name"),
                    fallbacks,
                    (Boolean) groqConfig.getOrDefault("log-requests", true),
                    (Boolean) groqConfig.getOrDefault("log-responses", true),
                    ((Number) groqConfig.getOrDefault("timeout", 30)).intValue(),
                    apiKey
            );

        } catch (IOException e) {
            throw new RuntimeException("Error cargando configuración de Groq desde YAML", e);
        }
    }

    /**
     * Carga un archivo YAML y lo convierte a Map
     */
    private static Map<String, Object> loadYaml(String resourceName) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        InputStream inputStream = LlmConfigLoader.class.getClassLoader()
                .getResourceAsStream(resourceName);

        if (inputStream == null) {
            throw new IOException("Archivo no encontrado: " + resourceName);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> config = mapper.readValue(inputStream, Map.class);
        return config;
    }
}


