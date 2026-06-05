package org.example.ai.groq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.exception.HttpException;
import dev.langchain4j.model.chat.ChatModel;
import org.example.ai.groq.config.GroqConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilidades para llamadas seguras a Groq (fallback automático a modelos alternativos)
 */
public class GroqUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String safeChat(GroqConfig config, String message) {
        List<String> models = new ArrayList<>();
        models.add(config.modelName());
        if (config.fallbackModels() != null) models.addAll(config.fallbackModels());

        for (String modelName : models) {
            ChatModel model = GroqClientFactory.buildChatModel(config, modelName);
            try {
                System.out.println("Intentando modelo: " + modelName);
                String resp = model.chat(message);
                return resp;
            } catch (HttpException he) {
                System.err.println("HttpException con modelo " + modelName + ": " + he.getMessage());
                if (isModelDecommissioned(he)) {
                    System.err.println("Modelo descontinuado: " + modelName + ", intentando siguiente fallback si existe...");
                    continue; // intentar siguiente modelo
                } else {
                    throw he;
                }
            } catch (Exception e) {
                System.err.println("Error al llamar al modelo " + modelName + ": " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Todos los modelos configurados fallaron (incluyendo fallbacks)");
    }

    public static boolean isModelDecommissioned(HttpException he) {
        try {
            String message = he.getMessage();
            JsonNode root = MAPPER.readTree(message);
            JsonNode error = root.path("error");
            String code = error.path("code").asText(null);
            return "model_decommissioned".equals(code);
        } catch (Exception ex) {
            // No se pudo parsear -> no asumimos model_decommissioned
            return false;
        }
    }
}

