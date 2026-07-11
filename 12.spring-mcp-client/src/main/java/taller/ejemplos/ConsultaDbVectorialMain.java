package taller.ejemplos;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.QueryFactory;
import io.qdrant.client.grpc.Points;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.transformers.TransformersEmbeddingModel;

import java.util.List;

public class ConsultaDbVectorialMain {

  //generar el modelo
  static float[] embedd(String text) throws Exception {
    var embeddingModel = new TransformersEmbeddingModel(MetadataMode.ALL);
    embeddingModel.setModelResource("classpath:models/model.onnx");
    embeddingModel.setTokenizerResource("classpath:tokenizer/tokenizer.json");
    embeddingModel.afterPropertiesSet();

    EmbeddingRequest reqauest = new EmbeddingRequest(List.of(text),  null);
    var response = embeddingModel.call(reqauest);

    return response.getResults().getFirst().getOutput();
  }


  static void main() throws Exception {

    QdrantClient client = new QdrantClient(
            QdrantGrpcClient.newBuilder("localhost", 6334, false)
                    .build());
    String texto = "requisitos para titulacion";
    float[] point = embedd(texto);

    var querySpec = Points.QueryPoints.newBuilder()
            .setCollectionName("springai")
            .setLimit(3) //los tres mas parecidos
            .setQuery(
                    QueryFactory.nearest(point)
            )
            .setWithPayload(
                    Points.WithPayloadSelector.newBuilder()
                            .setEnable(true)
                            .build()
            )
            .build();
    List<Points.ScoredPoint> results = client.queryAsync(querySpec).get();

    for(var it:results) {
      System.out.println("----------------------------------------");
      //System.out.println(it);

      var metadata = it.getPayload();

      System.out.println("score: " + it.getScore());
      System.out.println(metadata);
    }


  }
}
