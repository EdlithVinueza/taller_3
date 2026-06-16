plugins {
    java
    id("org.springframework.boot") version "3.2.5"
    // CAMBIA AQUÍ: Se actualizó de 1.1.4 a 1.1.7 para dar soporte a versiones nuevas de Gradle
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.example"
version = "unspecified"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    // Repositorio oficial para descargar los hitos de Spring AI (como el M7)
    maven { url = uri("https://spring.io") }
}

extra["springAiVersion"] = "2.0.0"

dependencies {
    // 1. CORREGIDO: Sin dos puntos ni puntos al final
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.ai:spring-ai-starter-model-openai")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // 2. CORREGIDO: Se cambió el grupo a org.apache.camel.springboot
    implementation("org.apache.camel.springboot:camel-file-starter:4.20.0")

    //--readers
    implementation("org.springframework.ai:spring-ai-pdf-document-reader")
    implementation("org.springframework.ai:spring-ai-tika-document-reader")

    //--Embeddings
    implementation("org.springframework.ai:spring-ai-starter-model-transformers")

    //--Vector Store
    implementation("org.springframework.ai:spring-ai-starter-vector-store-qdrant")

    //--EIPs
    implementation("org.apache.camel.springboot:camel-spring-boot-starter:4.20.0")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
    }
}
