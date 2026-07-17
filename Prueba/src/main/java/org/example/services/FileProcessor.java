package com.programacion.taller3.services;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

import java.io.File;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader; // Asegúrate de importar el lector de Tika
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class FileProcessor {

    public List<Document> procesar(File file) {
        Resource resource = new FileSystemResource(file);

        // Se comenta el lector anterior de PDF
        //PagePdfDocumentReader reader = new PagePdfDocumentReader(resource);

        // Se inicializa el lector de Apache Tika
        TikaDocumentReader reader = new TikaDocumentReader(resource);

        List<Document> documents = reader.get();

        System.out.println("Documentos creados: " + documents.size());
        System.out.println("Pagina 0");

        // Imprime el primer elemento de la lista extraída para evitar IndexOutOfBoundsException
        if (!documents.isEmpty()) {
            System.out.println(documents.get(0));
        }

        return documents;
    }
}

