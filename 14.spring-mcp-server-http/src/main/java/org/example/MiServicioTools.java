package org.example;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

/**
 * Servicio Spring que expone herramientas MCP a través del transporte HTTP (Streamable HTTP / SSE).
 */
@Service
public class MiServicioTools {

    /**
     * Herramienta MCP accesible vía HTTP para sumar dos números enteros.
     * 
     * @param num1 Primer valor entero.
     * @param num2 Segundo valor entero.
     * @return Cadena con el resultado de la operación.
     */
    @McpTool(description = "Add two numeric values")
    public String sumar(@McpToolParam Integer num1, @McpToolParam Integer num2){
        return String.valueOf(num1 + num2);
    }

}

