package org.example.services;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

//tranformador
@Component
public class TransformerProcessor {

    public List<Document> procesar(List<Document> documents) {
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .build();

        List<Document> splitted = splitter.split(documents);

        System.out.println("TransformerProcessor::documentos creados: " + splitted.size());

        System.out.println("Documento 0");
        System.out.println(splitted.getFirst());

        return splitted;
    }
}

