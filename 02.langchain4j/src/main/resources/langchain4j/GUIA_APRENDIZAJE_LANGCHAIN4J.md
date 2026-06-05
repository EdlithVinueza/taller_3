# 📚 Guía de Aprendizaje: LangChain4J

## Descripción General

Este módulo (02.langchain4j) es un **framework en Java para construir aplicaciones con IA**. Proporciona abstracciones de alto nivel para trabajar con modelos de lenguaje, embeddings y servicios de inteligencia artificial.

---

## 🎯 Orden de Aprendizaje Recomendado

### **NIVEL 1: FUNDAMENTOS DE EMBEDDINGS** (Conversión de Texto a Vectores)

#### 📌 01_EnbeddingModelMain.java
**¿QUÉ HACE?**
- Genera representaciones vectoriales (embeddings) de texto usando el modelo AllMiniLmL6V2
- Un embedding es un vector numérico de 384 dimensiones que representa el "significado" de un texto

**¿POR QUÉ?**
- Los modelos de IA trabajan con números, no con texto
- Los embeddings capturan el significado semántico del texto en formato numérico
- Esto permite comparar similitud entre textos, hacer búsquedas semánticas, etc.

**PROCESO PASO A PASO:**
1. Crear instancia del modelo de embeddings AllMiniLmL6V2
2. Pasar un texto al modelo: `"Hola, cómo estás?"`
3. El modelo retorna un objeto Response que contiene el Embedding
4. Extraer el vector del embedding (array de 384 números)
5. Imprimir la dimensión y los valores del vector

**SALIDA ESPERADA:**
```
Dimension: 384
[-0.123, 0.456, -0.789, ... 384 valores totales]
```

**FIN:** Entender que todo texto puede representarse como un vector numérico.

---

#### 📌 02_SimilitudMain.java
**¿QUÉ HACE?**
- Compara dos embeddings usando **similitud coseno**
- Calcula qué tan similar son dos textos en una escala de -1 a 1

**¿POR QUÉ?**
- Necesitamos una forma de medir si dos textos significan lo mismo
- La similitud coseno es la métrica estándar para comparar vectores en espacios altos
- Valores cercanos a 1 = textos muy similares
- Valores cercanos a -1 = textos muy diferentes

**PROCESO PASO A PASO:**
1. Crear 3 textos diferentes:
   - `"Cual es el area de un circulo de radio 5?"`
   - `"Obtiene la fecha y hora actual"`
   - `"Calcula el área de un círculo dado su radio"`
2. Generar embeddings para cada texto
3. Comparar e1 vs e2 (diferentes temas) → score bajo (~0.2)
4. Comparar e1 vs e3 (mismo tema) → score alto (~0.8)
5. Mostrar las similitudes

**SALIDA ESPERADA:**
```
hora vs fecha: 0.25
Circulo vs radio: 0.89
```

**FIN:** Entender que podemos medir qué tan similares son dos conceptos.

---

#### 📌 03_BusquedaSemanticaMain.java
**¿QUÉ HACE?**
- Busca en una lista de documentos el que más se parece a una consulta
- Implementa un buscador semántico simple sin base de datos

**¿POR QUÉ?**
- Necesitamos encontrar documentos relevantes sin palabras clave exactas
- "¿buscar por significado" en lugar de coincidencias de palabras
- Base para RAG (Retrieval Augmented Generation)

**PROCESO PASO A PASO:**
1. Tener una lista de 4 documentos de ejemplo
2. Generar embedding para cada documento
3. Generar embedding para la consulta: `"object oriented programming"`
4. Calcular similitud entre la consulta y cada documento
5. Encontrar el documento con la similitud más alta
6. Retornar el documento más relevante

**SALIDA ESPERADA:**
```
Documento 0 - Similitud: 0.92
Documento 1 - Similitud: 0.15
Documento 2 - Similitud: 0.08
Documento 3 - Similitud: 0.45
Mejor resultado: Java is an object-oriented programming language
Similaridad: 0.92
```

**FIN:** Implementar búsqueda semántica básica en memoria.

---

#### 📌 04_InMemoryEmbeddingStoreMain.java
**¿QUÉ HACE?**
- Almacena documentos y sus embeddings en memoria
- Permite búsquedas rápidas sin base de datos
- Retorna los Top-K (mejores) resultados

**¿POR QUÉ?**
- Es impracticable calcular similitud contra todos los documentos cada vez
- Necesitamos un almacén indexado para búsquedas rápidas
- InMemoryEmbeddingStore es perfecto para prototipos y apps pequeñas

**PROCESO PASO A PASO:**
1. Crear una tienda de embeddings vacía
2. Para cada documento:
   - Generar su embedding
   - Almacenarlo con una clave única
   - Guardarlo junto con el texto original (TextSegment)
3. Crear una consulta de búsqueda:
   - Generar embedding de la consulta
   - Especificar maxResults=2 (top-2)
4. Ejecutar búsqueda
5. Mostrar los matches con scores

**SALIDA ESPERADA:**
```
Score: 0.92 - Java is an object-oriented programming language
Score: 0.45 - Embeddings represent text as vectors
```

**FIN:** Implementar almacenamiento y búsqueda eficiente de embeddings.

---

### **NIVEL 2: INTERACCIÓN CON MODELOS DE LENGUAJE**

#### 📌 05_ChatMain.java
**¿QUÉ HACE?**
- Establece conexión con un modelo de lenguaje (LLM)
- Envía un mensaje y recibe una respuesta
- Es el "hola mundo" de LangChain4J

**¿POR QUÉ?**
- Necesitamos una forma estándar de comunicarnos con cualquier LLM
- LangChain4J abstrae las diferencias entre OpenAI, Ollama, etc.
- Permite cambiar de modelo solo modificando la configuración

**PROCESO PASO A PASO:**
1. Crear builder para OpenAiChatModel
2. Configurar:
   - apiKey: credencial de autenticación
   - modelName: "llama-2-7b-chat.Q4_0.gguf" (usar modelo local)
   - baseUrl: "http://localhost:8080" (servidor local Ollama)
3. Construir el modelo
4. Enviar mensaje: `"Qué es llama.cpp?"`
5. Recibir y mostrar respuesta

**CONFIGURACIÓN:**
```
Base URL: http://localhost:8080 (Ollama local)
Modelo: llama-2-7b-chat.Q4_0.gguf
API Key: No requiere si es local
```

**SALIDA ESPERADA:**
```
llama.cpp es un programa que permite ejecutar modelos de lenguaje...
```

**FIN:** Comunicarse con un LLM de forma simple y directa.

---

#### 📌 06_ChatAiServiceMain.java
**¿QUÉ HACE?**
- Crea un servicio de IA usando decoradores (@SystemMessage, @UserMessage)
- Encapsula la lógica de chat en una interfaz limpia
- Permite múltiples métodos con diferentes instrucciones del sistema

**¿POR QUÉ?**
- Separación de responsabilidades: el "qué" (instrucciones) del "cómo" (modelo)
- Reutilizable: puedes crear N servicios de IA diferentes
- Mejor para código profesional que llamadas directas al modelo

**PROCESO PASO A PASO:**
1. Definir interfaz `AsistenteLegal` con métodos:
   - `responder()`: asistente educativo
   - `consultar()`: asistente legal
2. Usar AiServices.create() para generar una implementación
3. Llamar los métodos como si fuera un objeto normal
4. AiServices automáticamente:
   - Inyecta el systemMessage
   - Inyecta el userMessage
   - Llama al modelo
   - Retorna el resultado

**VENTAJAS:**
- Type-safe: soporte de IDE
- Decoradores claros
- Fácil de testear

**FIN:** Crear servicios de IA reutilizables con interfaces limpias.

---

#### 📌 07_ChatMemoryMain.java
**¿QUÉ HACE?**
- Mantiene conversación multi-turno con memoria de contexto
- El modelo recuerda mensajes anteriores
- Lee usuario desde terminal interactiva

**¿POR QUÉ?**
- Sin memoria, cada mensaje es independiente
- Con memoria: conversaciones naturales como con un humano
- MessageWindowChatMemory mantiene los últimos N mensajes

**PROCESO PASO A PASO:**
1. Crear ChatMemory con window de 10 mensajes máximo
2. Usar AiServices.builder() con:
   - chatMemory(memory)
   - chatModel(model)
3. En loop infinito:
   - Leer entrada del usuario
   - Enviar al bot (AiServices inyecta automáticamente el historial)
   - Mostrar respuesta
   - Salir si escribe "exit"

**FLUJO DE CONVERSACIÓN:**
```
Usuario: ¿Cuál es la capital de Ecuador?
Bot: La capital de Ecuador es Quito.

Usuario: ¿Cuánta población tiene?
Bot: Quito tiene aproximadamente 2 millones de habitantes.
(El bot RECUERDA que hablamos de Quito)
```

**FIN:** Implementar chatbots con conversaciones contextuales.

---

#### 📌 08_ChatStreamingMain.java
**¿QUÉ HACE?**
- Recibe respuesta del modelo en tiempo real, palabra por palabra
- No espera a que termine toda la generación
- Muestra la respuesta conforme se va generando

**¿POR QUÉ?**
- Mejor UX: el usuario ve la respuesta de inmediato
- Modelos grandes pueden tardar 10+ segundos
- Streaming hace la app más responsiva

**PROCESO PASO A PASO:**
1. Usar OpenAiStreamingChatModel (no regular ChatModel)
2. Pasar un handler personalizado (MyStreamingChatResponseHandler)
3. El handler recibe callbacks:
   - `onPartialResponse()`: cada chunk de texto
   - `onCompleteResponse()`: cuando termina
   - `onError()`: si hay error
4. El handler imprime los chunks conforme llegan

**SALIDA EN TIEMPO REAL:**
```
llama.cpp es un programa... que permite... ejecutar modelos...
[Generación completa]
```

**FIN:** Implementar respuestas en streaming para mejor UX.

---

#### 📌 09_ChatStructuredOutputMain.java
**¿QUÉ HACE?**
- Extrae información estructurada (JSON) del texto
- Convierte respuesta en un objeto Java (Record)
- El modelo entiende qué estructura retornar

**¿POR QUÉ?**
- Raw text es difícil de procesar programáticamente
- Necesitamos datos estructurados para lógica posterior
- Extracción de información: NER, clasificación, etc.

**PROCESO PASO A PASO:**
1. Definir Record con campos deseados:
   ```java
   record Persona(String nombre, int edad, String ciudad) {}
   ```
2. Definir interfaz Extractor:
   ```java
   @UserMessage("Extrae la información de: {{texto}}")
   Persona extraerPersona(@V("texto") String texto);
   ```
3. AiServices.create() con esta interfaz
4. Llamar `extraerPersona("Soy María, tengo 30 años...")`
5. Recibir objeto Persona ya parseado

**ENTRADA:**
```
"Soy María, tengo 30 años y vivo en Quito"
```

**SALIDA:**
```
Persona(nombre=María, edad=30, ciudad=Quito)
```

**FIN:** Extraer datos estructurados usando IA (sin regex/parsing manual).

---

#### 📌 10_FunctionCallingMain.java
**¿QUÉ HACE?**
- El modelo puede "llamar funciones" (tools) para hacer cosas
- El modelo decide cuándo llamar cada herramienta
- El framework ejecuta las herramientas automáticamente

**¿POR QUÉ?**
- Los LLMs no pueden hacer cálculos reales
- Necesitan acceso a funciones externas
- El modelo decide qué función usar según la consulta

**PROCESO PASO A PASO:**
1. Definir clase Herramientas con métodos @Tool:
   - `obtenerFechaHora()`: retorna la hora actual
   - `calcularAreaCirculo(double radio)`: calcula área
2. Definir interfaz AsistenteConTools con @SystemMessage
3. Pasar herramientas a AiServices: `.tools(new Herramientas())`
4. Enviar consultas:
   - `"qué hora es?"` → el modelo llama obtenerFechaHora()
   - `"área de un círculo de radio 5?"` → llama calcularAreaCirculo(5)
5. El modelo integra los resultados en su respuesta

**FLUJO:**
```
Usuario: "¿Qué hora es?"
↓
Modelo decide: necesito función obtenerFechaHora()
↓
Framework ejecuta: obtenerFechaHora() → "2026-05-30T18:55:05"
↓
Modelo responde: "Son las 18:55:05"
```

**FIN:** Dar al modelo acceso a herramientas para hacer cosas reales.

---

## 📊 Diagrama de Dependencias

```
NIVEL 1: EMBEDDINGS
├─ 01_EnbeddingModelMain (Generar embeddings)
├─ 02_SimilitudMain (Comparar embeddings)
├─ 03_BusquedaSemanticaMain (Buscar documentos similares)
└─ 04_InMemoryEmbeddingStoreMain (Almacenar y recuperar)

NIVEL 2: CHAT & SERVICIOS
├─ 05_ChatMain (Chat básico)
├─ 06_ChatAiServiceMain (Servicios AI con decoradores)
├─ 07_ChatMemoryMain (Chat con memoria)
├─ 08_ChatStreamingMain (Respuestas en tiempo real)
├─ 09_ChatStructuredOutputMain (Extracción de datos)
└─ 10_FunctionCallingMain (Herramientas/Tools)
```

---

## 🔄 Conceptos Clave

| Concepto | Definición | Ejemplo |
|----------|-----------|---------|
| **Embedding** | Vector numérico que representa el significado de un texto | `[-0.12, 0.45, -0.78, ...]` |
| **Similitud Coseno** | Medida de similitud entre dos vectores (0-1) | `0.92` = muy similares |
| **Almacén de Embeddings** | Base de datos optimizada para búsquedas semánticas | InMemoryEmbeddingStore |
| **LLM** | Large Language Model (modelo de lenguaje) | llama-2, GPT-4, etc. |
| **Chat Model** | LLM configurado para diálogos | ChatMain |
| **AiServices** | Patrón de LangChain4J para crear servicios de IA | AiServices.create() |
| **Streaming** | Recibir respuesta palabra por palabra en tiempo real | ChatStreamingMain |
| **Structured Output** | Extraer datos en formato JSON/Record | ChatStructuredOutputMain |
| **Function Calling** | El modelo puede llamar funciones externas | FunctionCallingMain |

---

## 💡 Flujo Recomendado para Aprender

1. **Ejecuta 01_EnbeddingModelMain** → Entiende qué es un embedding
2. **Ejecuta 02_SimilitudMain** → Entiende similitud coseno
3. **Ejecuta 03_BusquedaSemanticaMain** → Implementa búsqueda manual
4. **Ejecuta 04_InMemoryEmbeddingStoreMain** → Usa almacén
5. **Ejecuta 05_ChatMain** → Conecta con LLM
6. **Ejecuta 06_ChatAiServiceMain** → Usa servicios
7. **Ejecuta 07_ChatMemoryMain** → Agrega contexto
8. **Ejecuta 08_ChatStreamingMain** → Respuestas en tiempo real
9. **Ejecuta 09_ChatStructuredOutputMain** → Extrae datos
10. **Ejecuta 10_FunctionCallingMain** → Agrega herramientas

---

## 🚀 Próximos Pasos

### Después de LangChain4J (02), aprende:
- **03.spring-ai**: Framework de Spring para integración con IA en aplicaciones web

### Combinar todo:
- RAG: Embeddings + Búsqueda + Chat
- Chatbot inteligente con memoria y herramientas
- Extractor de datos automático
- Asistente legal especializado

---

## 📝 Archivo de Configuración

**BaseUrl:** `http://localhost:8080`
**Modelo:** `llama-2-7b-chat.Q4_0.gguf`
**API Key:** Se puede obtener del modelo local

Asegúrate de tener **Ollama corriendo** en localhost:8080 antes de ejecutar.

---

## 🎓 Resumen

- **Nivel 1** te enseña: cómo trabajar con embeddings y búsqueda semántica
- **Nivel 2** te enseña: cómo interactuar con modelos de lenguaje de forma cada vez más sofisticada
- Al final, sabes construir aplicaciones IA complejas en Java

**¡Bienvenido al futuro del desarrollo con IA! 🤖**
