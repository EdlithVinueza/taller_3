package com.programacion.taller3.services;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingProcessor {

    public void procesar(List<Document> documents) {
        System.out.println("EmbeddingProcessor::procesar documentos: " + documents.size());
    }
}

