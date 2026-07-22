package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de inicio para el Servidor MCP basado en HTTP (WebMVC / Streamable SSE).
 * 
 * NOTA: Aunque la clase se llame 'SpringMcpStdioServerMain', este proyecto expone las herramientas MCP
 * como un servicio web HTTP mediante la dependencia 'spring-ai-starter-mcp-server-webmvc', escuchando
 * en el puerto configurado (ej. 3001) y en el endpoint /mcp.
 */
@SpringBootApplication
public class SpringMcpHttpServerMain {
    public static void main(String[] args) {
        // Inicializa la aplicación Spring Boot con servidor web activo
        SpringApplication.run(SpringMcpHttpServerMain.class, args);
    }
}

