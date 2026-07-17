package org.example.routers;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileReaderRouter extends RouteBuilder {


    @Value("${app.files.inbound:/home/edlith/tools/springai}")
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
