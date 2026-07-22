package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de inicio para el Servidor MCP basado en STDIO.
 * 
 * NOTA: Aunque la clase se llame 'SpringMcpHttpServerMain', en este proyecto la aplicación está
 * configurada para ejecutarse en modo no-web (STDIO), comunicándose con el Cliente MCP a través
 * de la entrada y salida estándar (Standard Input/Output JSON-RPC).
 */
@SpringBootApplication
public class SpringMcpStdioServerMain {
    public static void main(String[] args) {
        // Inicializa y arranca la aplicación Spring Boot
        SpringApplication.run(SpringMcpStdioServerMain.class, args);
    }
}

