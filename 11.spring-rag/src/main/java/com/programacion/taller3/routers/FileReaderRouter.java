package com.programacion.taller3.routers;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileReaderRouter extends RouteBuilder {

    // Se mantiene el formato exacto de tu docente, pero apuntando a tu carpeta en Windows
    @Value("${app.files.inbound:C:/tools/springai}")
    String inboundPath;

    @Override
    public void configure() throws Exception {

        String from = "file:%s?antInclude=*.pdf&delay=1000&move=procesados".formatted(inboundPath);

        from(from)
                .log("Archivo leido: ${header.CamelFileName}")
                .bean("fileProcessor")
                .bean("transformerProcessor")
                .bean("embeddingProcessor");
        ;
    }
}
