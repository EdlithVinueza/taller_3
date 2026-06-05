# Resumen teórico de `llama.cpp` y `LangChain4j`

## 1. ¿Qué es `llama.cpp`?

`llama.cpp` es un proyecto en **C++** que permite ejecutar modelos de lenguaje grandes (LLMs) **de forma local** y muy optimizada, normalmente en CPU.

### Idea principal
En lugar de depender siempre de una API en la nube, `llama.cpp` permite correr modelos descargados en tu propia máquina.

### Ventajas
- Funciona localmente
- No siempre necesita Internet
- Puede correr en equipos modestos
- Está optimizado para modelos cuantizados, por ejemplo `.gguf`
- Se usa como base en herramientas locales como Ollama

### Relación con este módulo
En `02.langchain4j`, el ejemplo de `ChatMain.java` se conecta a un servidor local tipo Ollama y usa un modelo como `llama-2-7b-chat.Q4_0.gguf`.

---

## 2. ¿Qué es `LangChain4j`?

`LangChain4j` es una librería de **Java** para construir aplicaciones con IA generativa.

### Sirve para
- chat con modelos LLM
- embeddings
- memoria conversacional
- herramientas / function calling
- salida estructurada
- streaming
- búsqueda semántica y RAG

### Idea principal
Te simplifica la integración con modelos de IA y te permite trabajar con una API más limpia y declarativa.

---

## 3. Componentes principales

### `ChatLanguageModel`
Interfaz para modelos de chat.

**Qué hace:**
- recibe un mensaje
- devuelve una respuesta del modelo

**En el proyecto:**
- `ChatMain.java`

**Uso:**
- `model.chat("Qué es llama.cpp?")`

---

### `EmbeddingModel`
Convierte texto en vectores numéricos llamados **embeddings**.

**Qué hace:**
- transforma texto en números que representan su significado

**Para qué sirve:**
- búsqueda semántica
- similitud de textos
- clustering
- recomendación
- RAG

**En el proyecto:**
- `EnbeddingModelMain.java`
- `SimilitudMain.java`

**Uso:**
- `model.embed(texto)`
- comparación con `CosineSimilarity`

---

### `TokenCountEstimator`
Estima cuántos tokens consumirá un texto o una conversación.

**Para qué sirve:**
- controlar costos
- evitar pasar límites de contexto
- optimizar memoria conversacional

**En el proyecto:**
- no aparece implementado directamente
- sí está relacionado conceptualmente con memoria por tokens

---

### `ChatMemory`
Gestiona el historial de conversación.

**Qué hace:**
- guarda mensajes previos para que el modelo tenga contexto

**En el proyecto:**
- `ChatMemoryMain.java`

**Uso:**
```java
ChatMemory memory = MessageWindowChatMemory.builder()
        .maxMessages(10)
        .build();
```

Luego se inyecta en `AiServices.builder(...).chatMemory(memory)`.

---

### `StreamingChatLanguageModel`
Permite recibir la respuesta del modelo en streaming, poco a poco.

**Para qué sirve:**
- mostrar la respuesta mientras se genera
- mejorar la experiencia de usuario

**En el proyecto:**
- no está implementado directamente

---

### `AiServices`
Permite crear asistentes de IA a partir de **interfaces Java**.

**Qué hace:**
- genera la implementación en tiempo de ejecución
- inyecta prompts, parámetros, memoria y herramientas
- simplifica la lógica declarativa

**En el proyecto:**
- `AsistenteLegal.java`
- `ChatAiServiceMain.java`
- `ChatMemoryMain.java`
- `FunctionCallingMain.java`
- `ChatStructuredOutputMain.java`

---

## 4. ¿Dónde se usan en el proyecto?

| Componente | Archivo | Uso |
|---|---|---|
| `ChatLanguageModel` | `ChatMain.java` | Chat básico con un modelo LLM |
| `EmbeddingModel` | `EnbeddingModelMain.java`, `SimilitudMain.java` | Generación y comparación de embeddings |
| `TokenCountEstimator` | — | No usado directamente |
| `ChatMemory` | `ChatMemoryMain.java` | Memoria de conversación |
| `StreamingChatLanguageModel` | — | No usado directamente |
| `AiServices` | `ChatAiServiceMain.java`, `FunctionCallingMain.java`, `ChatMemoryMain.java`, `ChatStructuredOutputMain.java` | Servicios declarativos de IA |

---

## 5. Ejemplos del módulo

### `ChatMain.java`
Ejemplo de chat básico sin memoria.

### `ChatAiServiceMain.java`
Ejemplo de servicio declarativo usando `AiServices`.

### `AsistenteLegal.java`
Interfaz decorada con:
- `@SystemMessage`
- `@UserMessage`
- `@V("pregunta")`

### `ChatMemoryMain.java`
Ejemplo de chatbot con historial.

### `FunctionCallingMain.java`
Ejemplo de uso de herramientas con `@Tool`.

### `ChatStructuredOutputMain.java`
Ejemplo de salida estructurada usando `Record`.

### `EnbeddingModelMain.java`
Genera embeddings para un texto.

### `SimilitudMain.java`
Compara embeddings con similitud coseno.

---

## 6. Resumen corto para estudiar

- **`llama.cpp`**: motor local en C++ para ejecutar LLMs.
- **`LangChain4j`**: librería Java para integrar IA generativa.
- **`ChatLanguageModel`**: chat simple con un modelo.
- **`EmbeddingModel`**: convierte texto en vectores.
- **`TokenCountEstimator`**: estima tokens.
- **`ChatMemory`**: guarda historial de conversación.
- **`StreamingChatLanguageModel`**: respuesta en tiempo real.
- **`AiServices`**: crea asistentes declarativos desde interfaces Java.

---

## 7. Idea clave del módulo

Este módulo muestra cómo usar Java con LangChain4j para:
- chatear con un modelo
- generar embeddings
- comparar similitud semántica
- conservar memoria conversacional
- usar herramientas automáticas
- extraer salida estructurada

En otras palabras: **convierte un modelo de IA en componentes reutilizables dentro de Java**.

