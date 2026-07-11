package com.programacion.taller3;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.QueryFactory;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Points;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.transformers.TransformersEmbeddingModel;

import java.util.List;

public class ConsultaDbVectorialMain {

    static float[] embedd(String text) throws Exception {
        var embeddingModel = new TransformersEmbeddingModel(MetadataMode.ALL);
        embeddingModel.setModelResource("classpath:models/model.onnx");
        embeddingModel.setTokenizerResource("classpath:models/tokenizer.json");
        embeddingModel.afterPropertiesSet();

        EmbeddingRequest reqauest = new EmbeddingRequest(List.of(text), null);
        var response = embeddingModel.call(reqauest);
        return response.getResults().getFirst().getOutput();
    }

    public static void main(String[] args) throws Exception {
        QdrantClient client = new QdrantClient(
                QdrantGrpcClient.newBuilder("localhost", 6334, false).build());

        String collectionName = "springai";

        // 1. PASO NUEVO: Verificar si la colección ya existe para no duplicarla
        var collectionsList = client.listCollectionsAsync().get();
        boolean exists = collectionsList.contains(collectionName);

        if (!exists) {
            // 2. PASO NUEVO: Crear la colección si no existe
            // NOTA: Ajusta el valor .setSize(384) si tu modelo ONNX usa otra dimensión (ej: 768)
            client.createCollectionAsync(
                    collectionName,
                    VectorParams.newBuilder()
                            .setDistance(Distance.Cosine)
                            .setSize(384)
                            .build()
            ).get();
            System.out.println("Colección '" + collectionName + "' creada automáticamente por primera vez.");
        }

        String texto = "requisitos para titulación";
        float[] point = embedd(texto);

        var querySpec = Points.QueryPoints.newBuilder()
                .setCollectionName(collectionName)
                .setLimit(3)
                .setQuery(QueryFactory.nearest(point))
                .setWithPayload(
                        Points.WithPayloadSelector.newBuilder()
                                .setEnable(true)
                                .build()
                )
                .build();

        List<Points.ScoredPoint> results = client.queryAsync(querySpec).get();

        for(var it : results) {
            System.out.println("---------------------------------");
            System.out.println(it);
            System.out.println("score: " + it.getScore());
        }
    }
}
