import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileReaderRouter extends RouteBuilder {

    @Value("${app.files.inbound:C:/tools/springai}")
    String inboundPath;

    @Override
    public void configure() throws Exception {

        // Construye la URI usando barras normales (/) válidas para las rutas de Apache Camel
        String from = "file:%s?include=*.pdf&delay=1000&move=procesados".formatted(inboundPath);

        // Se usa la variable 'from' dinámica en lugar del "file:input?noop=true" fijo
        from(from)
                .log("Archivo leido: ${header.CamelFileName}")
                //.to("direct:processFile");
    }
}
