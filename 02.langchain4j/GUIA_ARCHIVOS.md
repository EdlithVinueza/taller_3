# 📚 Guía Rápida de Archivos - 02.langchain4j

## Estructura de Aprendizaje

Este módulo está organizado en **2 niveles de complejidad**:

---

## NIVEL 1: EMBEDDINGS (Fundamentos)

### 📌 01_EnbeddingModelMain.java
**Ubicación:** `src/main/java/com/programacion/embeddigs/`

**¿Qué hace?**
- Genera un embedding (vector numérico) a partir de texto
- Un embedding es la representación vectorial del significado del texto

**Concepto clave:** 
```
Texto: "Hola, cómo estás?" 
  → Modelo AllMiniLmL6V2
  → Embedding: [-0.12, 0.45, -0.78, ..., 384 números]
```

**Salida:** 
```
Dimension: 384
[-0.123, 0.456, ...]
```

---

### 📌 02_SimilitudMain.java
**Ubicación:** `src/main/java/com/programacion/embeddigs/`

**¿Qué hace?**
- Compara dos embeddings usando **similitud coseno**
- Mide qué tan parecidos son dos textos (0-1)

**Concepto clave:** 
```
Texto 1: "Área de un círculo de radio 5"
Texto 2: "Calcula el área de un círculo"
  → Similitud: 0.89 (muy similar)

Texto 1: "Área de un círculo de radio 5"
Texto 2: "¿Qué hora es?"
  → Similitud: 0.25 (muy diferente)
```

---

### 📌 03_BusquedaSemanticaMain.java
**Ubicación:** `src/main/java/com/programacion/embeddigs/`

**¿Qué hace?**
- Busca en una lista de documentos el más similar a una consulta
- Búsqueda semántica SIN base de datos

**Concepto clave:**
```
Documentos: ["Java es OOP", "Python es para IA", "Café en cocina", "Embeddings"]
Consulta: "object oriented programming"
  → Resultado: "Java es OOP" (similitud: 0.92)
```

---

### 📌 04_InMemoryEmbeddingStoreMain.java
**Ubicación:** `src/main/java/com/programacion/embeddigs/`

**¿Qué hace?**
- Almacena documentos + embeddings en memoria
- Búsquedas rápidas sin iterar manualmente

**Concepto clave:**
```
Almacén:
  doc-1: "Java es OOP" → [embedding]
  doc-2: "Python es IA" → [embedding]
  doc-3: "Café" → [embedding]

Búsqueda top-2:
  Consulta: "object oriented"
  Resultados:
    - doc-1: score 0.92
    - doc-3: score 0.45
```

---

## NIVEL 2: CHAT & SERVICIOS (Modelos de Lenguaje)

### 📌 05_ChatMain.java
**Ubicación:** `src/main/java/com/programacion/taller3/`

**¿Qué hace?**
- Chat básico: envía mensaje y recibe respuesta
- Factory method `chatModel()` reutilizable

**Concepto clave:**
```
Pregunta: "¿Qué es llama.cpp?"
  → Modelo: llama-2-7b-chat
  → Respuesta: "Un programa que ejecuta modelos LLaMA..."
```

**Requisito:** Ollama corriendo en `localhost:8080`

---

### 📌 06_ChatAiServiceMain.java
**Ubicación:** `src/main/java/com/programacion/taller3/`

**¿Qué hace?**
- Crea servicios de IA usando **decoradores** en interfaces
- Patrón profesional: código limpio y reutilizable

**Concepto clave:**
```
@SystemMessage("Eres abogado...")
@UserMessage("Pregunta: {{pregunta}}")
String consultar(@V("pregunta") String pregunta);

AiServices.create() → genera implementación automáticamente
```

**Archivos relacionados:**
- `utils/AsistenteLegal.java` - Interfaz con decoradores

---

### 📌 07_ChatMemoryMain.java
**Ubicación:** `src/main/java/com/programacion/taller3/`

**¿Qué hace?**
- Chat multi-turno CON memoria de contexto
- El modelo recuerda mensajes anteriores

**Concepto clave:**
```
Usuario: "¿Cuál es la capital de Ecuador?"
Bot: "Quito"

Usuario: "¿Cuánta población tiene?"
Bot: "Quito tiene 2 millones..." 
     (Bot RECUERDA que hablamos de Quito)
```

**Características:**
- Loop interactivo
- `Scanner` para input del usuario
- `MessageWindowChatMemory` para contexto

---

### 📌 08_ChatStreamingMain.java
**Ubicación:** `src/main/java/com/programacion/taller3/`

**¿Qué hace?**
- Respuestas en STREAMING (palabra por palabra)
- No espera respuesta completa

**Concepto clave:**
```
En tiempo real:
"Un..." → "Un embedding..." → "Un embedding es..."
         (Usuario ve texto conforme se genera)
```

**Archivos relacionados:**
- `utils/MyStreamingChatResponseHandler.java` - Handler para callbacks

---

### 📌 09_ChatStructuredOutputMain.java
**Ubicación:** `src/main/java/com/programacion/taller3/`

**¿Qué hace?**
- Extrae datos ESTRUCTURADOS (JSON/Record) del texto
- Convierte texto libre en objeto tipado

**Concepto clave:**
```
Entrada: "Soy María, tengo 30 años y vivo en Quito"
  → Modelo entiende qué extraer
  → Record: Persona(nombre=María, edad=30, ciudad=Quito)

Salida: Objeto Java tipo-safe
```

**Ventaja:** Programación sin parsing manual (sin regex)

---

### 📌 10_FunctionCallingMain.java
**Ubicación:** `src/main/java/com/programacion/taller3/`

**¿Qué hace?**
- El modelo puede "llamar funciones" (tools)
- Modelo decide qué función usar

**Concepto clave:**
```
Usuario: "¿Qué hora es?"
  → Modelo: "Necesito llamar obtenerFechaHora()"
  → Framework ejecuta la función
  → Modelo recibe: "2026-05-30T18:55:05"
  → Respuesta: "Son las 18:55:05"
```

**Herramientas en este ejemplo:**
- `obtenerFechaHora()` - hora actual
- `calcularAreaCirculo(double)` - cálculo matemático

---

## 🛠️ Archivos de Utilidad

### `utils/AsistenteLegal.java`
- Interfaz con decoradores `@SystemMessage` y `@UserMessage`
- Usada por `ChatAiServiceMain`
- Dos métodos: `responder()` y `consultar()`

### `utils/MyStreamingChatResponseHandler.java`
- Implementa `StreamingChatResponseHandler`
- Callbacks: `onPartialResponse()`, `onCompleteResponse()`, `onError()`
- Usada por `ChatStreamingMain`

---

## 📊 Tabla Comparativa

| Archivo | Concepto | Complejidad | Requiere |
|---------|----------|-------------|----------|
| EnbeddingModelMain | Embeddings | ⭐ | Modelo local |
| SimilitudMain | Similitud coseno | ⭐ | Modelo local |
| BusquedaSemanticaMain | Búsqueda manual | ⭐⭐ | Modelo local |
| InMemoryEmbeddingStoreMain | Almacén indexado | ⭐⭐ | Modelo local |
| ChatMain | Chat básico | ⭐⭐ | Ollama |
| ChatAiServiceMain | Servicios AI | ⭐⭐⭐ | Ollama, AsistenteLegal |
| ChatMemoryMain | Chat con contexto | ⭐⭐⭐ | Ollama, entrada user |
| ChatStreamingMain | Streaming | ⭐⭐⭐ | Ollama, handler |
| ChatStructuredOutputMain | Extracción datos | ⭐⭐⭐⭐ | Ollama, Record |
| FunctionCallingMain | Tools/Functions | ⭐⭐⭐⭐ | Ollama, @Tool |

---

## 🚀 Orden de Ejecución Recomendado

1. **01_EnbeddingModelMain** - Entiende embeddings
2. **02_SimilitudMain** - Entiende similitud
3. **03_BusquedaSemanticaMain** - Búsqueda manual
4. **04_InMemoryEmbeddingStoreMain** - Almacén
5. **05_ChatMain** - Chat básico
6. **06_ChatAiServiceMain** - Servicios
7. **07_ChatMemoryMain** - Conversación interactiva
8. **08_ChatStreamingMain** - Streaming
9. **09_ChatStructuredOutputMain** - Extracción
10. **10_FunctionCallingMain** - Tools

---

## ⚙️ Configuración General

**Modelo:** `llama-2-7b-chat.Q4_0.gguf`  
**Servidor:** `http://localhost:8080`  
**Framework:** Ollama (instancia local)

### Comandos Útiles (Ollama)

```bash
# Verificar que Ollama está corriendo
curl http://localhost:8080/api/tags

# Ver modelos disponibles
ollama list

# Descargar modelo llama2
ollama pull llama2

# Ejecutar con Ollama
ollama serve

# En otra terminal, probar conexión
curl -X POST http://localhost:8080/api/generate \
  -d '{"model":"llama2","prompt":"Hola"}'
```

---

## 📝 Notas Importantes

- **AllMiniLmL6V2:** Modelo de embeddings eficiente, 384 dimensiones
- **OpenAiChatModel:** Compatible con cualquier servidor (OpenAI, Ollama, etc.)
- **AiServices:** Generación de código en tiempo de ejecución
- **Streaming:** Mejor UX pero más complejo
- **Function Calling:** Requiere que modelo entienda JSON Schema

---

## 🔍 Debugging

Si un archivo falla:

1. **Verificar Ollama:** `curl http://localhost:8080/api/tags`
2. **Ver logs:** Active `logRequests(true)` y `logResponses(true)`
3. **Probar modelo:** `ollama run llama2 "Hola"`
4. **Stack trace:** Busca línea de error en archivo
5. **Consulta guía:** Ver GUIA_APRENDIZAJE_LANGCHAIN4J.md

---

## 💡 Próximos Pasos

### Después de dominar estos 10 archivos:

1. **Combinar conceptos:**
   - RAG: Embeddings + Chat + Búsqueda
   - Chatbot: Chat + Memory + Tools
   - Extractor: StructuredOutput + Tools

2. **Escalabilidad:**
   - Pasar a vector DB (Pinecone, Weaviate)
   - Persistencia en BD (MongoDB)
   - Multi-user sessions

3. **Integración:**
   - Spring Boot (03.spring-ai)
   - REST APIs
   - Microservicios

4. **Seguridad:**
   - Autenticación de herramientas
   - Rate limiting
   - Auditoría de uso

---

**¡Felicidades al completar este nivel de LangChain4J! 🎓**
