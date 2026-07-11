plugins {
    java
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.example"
version = "unspecified"

repositories {
    mavenCentral()
}

// Configuración de la versión de Spring AI
val springAiVersion = "2.0.0"

dependencyManagement {
    imports {
        // Se inyecta la variable local de forma directa y limpia
        mavenBom("org.springframework.ai:spring-ai-bom:$springAiVersion")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.ai:spring-ai-starter-model-openai")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

//Leer archivos PDF -- readers
    implementation("org.springframework.ai:spring-ai-pdf-document-reader")
//Trabajar con DocumentReader, PdfDocumentReader
    implementation("org.springframework.ai:spring-ai-tika-document-reader")

//--Embeddings
    implementation("org.springframework.ai:spring-ai-starter-model-transformers")

//--Vector Store
    implementation("org.springframework.ai:spring-ai-starter-vector-store-qdrant")
//incrementamos
    implementation("org.springframework.ai:spring-ai-vector-store-advisor")


// camel -- EIPS
    implementation("org.apache.camel.springboot:camel-spring-boot-starter:4.20.0")
    implementation("org.apache.camel.springboot:camel-file-starter:4.20.0")
    implementation("org.springframework.ai:spring-ai-starter-mcp-client")

//    //--Docker compose
    // runtimeOnly("org.springframework.boot:spring-boot-docker-compose")

    // implementation("com.microsoft.onnxruntime:onnxruntime_gpu:1.26.0")

}

tasks.test {
    useJUnitPlatform()
}
