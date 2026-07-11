package taller.services;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransformerProcessor {
  public List<Document> procesar(List<Document> documents) {
    TokenTextSplitter splitter = TokenTextSplitter.builder().build();
    List<Document> splitted = splitter.split(documents);

    System.out.println("TransformersProcessor::Documentos creados: " + documents.size());
//        System.out.println("Pagina 0");
//        System.out.println(documents.get(15));

    return splitted;

  }
}
