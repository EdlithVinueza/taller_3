package org.example.ai.groq.config;

/**
 * RECORD: Representa la configuración de Groq
 *
 * Un record es como una clase pero más concisa:
 * - Genera automáticamente constructores, getters, equals, hashCode, toString
 * - Inmutable (final)
 * - Perfecto para DTOs (Data Transfer Objects)
 */
import java.util.List;

public record GroqConfig(
        String baseUrl,
        String modelName,
        List<String> fallbackModels,
        boolean logRequests,
        boolean logResponses,
        int timeout,
        String apiKey
) {
    @Override
    public String toString() {
        // Ocultar la API key en los logs (por seguridad)
        return "GroqConfig{" +
                "baseUrl='" + baseUrl + '\'' +
                ", modelName='" + modelName + '\'' +
                ", fallbackModels=" + fallbackModels +
                ", logRequests=" + logRequests +
                ", logResponses=" + logResponses +
                ", timeout=" + timeout +
                ", apiKey='" + (apiKey != null && apiKey.length() > 10 ? apiKey.substring(0, 10) + "..." : "null") + '\'' +
                '}';
    }
}

