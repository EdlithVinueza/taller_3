package org.example;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class MiServicioTools {

    @McpTool(description = "Add two numeric values")
    public String sumar(@McpToolParam Integer num1,@McpToolParam Integer num2){
        return String.valueOf(num1+num2);
    }

}
