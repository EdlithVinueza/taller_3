package org.example;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Servicio Spring que expone funciones u herramientas (Tools) para ser consumidas por un cliente MCP.
 * 
 * Mediante las anotaciones de Spring AI (@McpTool y @McpToolParam), estas funciones son
 * registradas automáticamente en el protocolo MCP y descubiertas por clientes de IA como ChatClient.
 */
@Service
public class MiServicioTools {

    /**
     * Herramienta MCP para sumar dos valores numéricos enteros.
     * 
     * @param num1 Primer número a sumar.
     * @param num2 Segundo número a sumar.
     * @return Cadena con el resultado de la suma.
     */
    @McpTool(description = "Add two numeric values")
    public String sumar(@McpToolParam Integer num1, @McpToolParam Integer num2){
        return String.valueOf(num1 + num2);
    }

    /**
     * Herramienta MCP que devuelve la fecha/hora actual del sistema.
     * 
     * @return Cadena de texto con la fecha actual en formato ISO-8601 (YYYY-MM-DD).
     */
    @McpTool(description = "Obtiene la hora Acutal")
    public String horaActual(){
        return LocalDate.now().toString();
    }

}

