package org.example.services;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader; // Asegúrate de importar el lector de Tika
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

//precesador de archivos
@Component
public class FileProcessor {

    public List<Document> procesar(File file) {
        Resource resource = new FileSystemResource(file);

        TikaDocumentReader reader = new TikaDocumentReader(resource);

        List<Document> documents = reader.get();

        System.out.println("documentos creados: " + documents.size());
        System.out.println("Pagina 0");

         if (!documents.isEmpty()) {
            System.out.println(documents.get(0));
        }

        return documents;
    }
}

