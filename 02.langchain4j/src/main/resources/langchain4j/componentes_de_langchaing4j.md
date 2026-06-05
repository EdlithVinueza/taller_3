## Explicación de cada componente de LangChain4j

Te explico **qué hace cada uno** de forma sencilla y con ejemplos prácticos:

---

## 1. ChatLanguageModel

**¿Qué hace?** Interfaz para enviar mensajes a un modelo de chat y recibir respuestas.

**Analogía:** Es como tener un **WhatsApp con una IA** - tú le escribes, ella te responde.

**Ejemplo:**
```kotlin
val model: ChatLanguageModel = // tu configuración

// Envío simple
val respuesta = model.chat("¿Qué es RAG?")
println(respuesta) // "RAG es una técnica para mejorar respuestas..."

// Con historial (varios mensajes)
val respuesta2 = model.chat(
    UserMessage("¿Cómo estás?"),
    AiMessage("Estoy bien, ¿y tú?"),
    UserMessage("¿Qué hora es?")
)
```

**Usa esto cuando:** Necesitas conversaciones simples pregunta-respuesta.

---

## 2. EmbeddingModel

**¿Qué hace?** Convierte texto en vectores (listas de números) para buscar similitudes.

**Analogía:** Es como **tomar huellas digitales de un texto** - textos similares tienen huellas parecidas.

**Ejemplo:**
```kotlin
val embeddingModel: EmbeddingModel = AllMiniLmL6V2EmbeddingModel()

// Generar vector de un texto
val embedding = embeddingModel.embed("Me gustan los perros").content()
println(embedding.vectorAsList()) // [0.12, -0.05, 0.33, ...]

// Comparar dos textos
val texto1 = embeddingModel.embed("Me encantan los caninos").content()
val texto2 = embeddingModel.embed("El clima está soleado").content()

val similitud = cosineSimilarity(texto1, texto2)
println(similitud) // 0.85 (más cercano a 1 = más parecido)
```

**Usa esto cuando:** Necesitas buscar documentos parecidos o hacer RAG.

---

## 3. TokenCountEstimator

**¿Qué hace?** Cuenta cuántos tokens (trozos de palabras) tiene un texto.

**Analogía:** Es como un **contador de calorías pero para textos** - te dice cuánto "pesa" tu mensaje.

**Ejemplo:**
```kotlin
val tokenCounter: TokenCountEstimator = OpenAiTokenCountEstimator()

val texto = "Hola mundo, esto es una prueba"
val tokens = tokenCounter.estimateTokenCount(texto)
println("Este texto usa $tokens tokens") // Ejemplo: "Este texto usa 7 tokens"

// Útil para saber si te pasas del límite
val limite = 4096
if (tokens > limite) {
    println("Texto demasiado largo, necesitas resumirlo")
}
```

**Usa esto cuando:** Quieres controlar costos (APIs de pago) o evitar exceder límites de contexto.

---

## 4. ChatMemory

**¿Qué hace?** Guarda el historial de la conversación para que el modelo recuerde lo que dijeron antes.

**Analogía:** Es como la **memoria a corto plazo** de una persona - recuerda lo que dijiste hace 5 minutos.

**Ejemplo:**
```kotlin
// Sin memoria (cada pregunta es independiente)
val model: ChatLanguageModel = // ...
model.chat("Me llamo Juan")
model.chat("¿Cómo me llamo?") // No lo sabe 😢

// CON memoria
val memory = MessageWindowChatMemory.builder()
    .maxMessages(10)  // recuerda los últimos 10 mensajes
    .build()

memory.add(UserMessage("Me llamo Juan"))
memory.add(AiMessage("Encantado, Juan"))

// Al enviar, incluye el historial automáticamente
val respuesta = model.chat(memory.messages())
// El modelo responde sabiendo que te llamas Juan
```

**Tipos de memoria:**
| Tipo | Qué hace |
|------|----------|
| `MessageWindowChatMemory` | Recuerda los últimos N mensajes |
| `TokenWindowChatMemory` | Recuerda hasta N tokens (ej: 4000) |
| `ConversationChainMemory` | Recuerda todo pero más pesado |

**Usa esto cuando:** Quieres conversaciones con contexto (ej: un chatbot que recuerde lo que preguntaste antes).

---

## 5. StreamingChatLanguageModel

**¿Qué hace?** Igual que ChatLanguageModel, pero la respuesta llega **poco a poco** (en tiempo real).

**Analogía:** Es como **ver un video mientras se descarga** en lugar de esperar a que se descargue completo.

**Ejemplo:**
```kotlin
val streamingModel: StreamingChatLanguageModel = // ...

// Necesitas un manejador que procese cada pedacito
streamingModel.chat("Cuéntame una historia larga", 
    object : StreamingChatResponseHandler {
        override fun onPartialResponse(partialResponse: String) {
            // Cada palabra o frase llega aquí
            print(partialResponse) // "Había" + " una" + " vez..."
        }
        
        override fun onCompleteResponse(completeResponse: ChatResponse) {
            println("\n[Historia completa]")
        }
        
        override fun onError(error: Throwable) {
            println("Error: ${error.message}")
        }
    }
)

// Con Kotlin lambda (más elegante)
streamingModel.chat("Dime un chiste") { partialResponse ->
    print(partialResponse) // "¿Qué" + " le" + " dice"...
}
```

**Comparación:**
| | Normal | Streaming |
|--|--------|-----------|
| **Velocidad percepción** | Lenta (esperas todo) | Rápida (ves progreso) |
| **Uso de memoria** | Menos | Más |
| **UX** | Buena para respuestas cortas | Mejor para respuestas largas |

**Usa esto cuando:** Respuestas largas (ensayos, código, explicaciones) o quieres mejor experiencia de usuario.

---

## 6. AiService

**¿Qué hace?** Es una **fábrica mágica** que convierte interfaces Java/Kotlin en clientes de IA.

**Analogía:** Es como tener un **asistente personal** - tú defines qué quieres y él se encarga de todo.

**Ejemplo básico:**
```kotlin
// 1. Define qué quieres (una interfaz)
interface Traductor {
    fun traducirAlIngles(texto: String): String
}

// 2. AiService crea la implementación automáticamente
val traductor = AiServices.create(Traductor::class.java, chatModel)

// 3. Usas tu interfaz como si fuera una función normal
val result = traductor.traducirAlIngles("Hola mundo")
// Internamente: construye prompt, llama al modelo, parsea respuesta
println(result) // "Hello world"
```

**Ejemplo avanzado:**
```kotlin
interface AsistenteLegal {
    @SystemMessage("Eres un abogado experto en derecho ecuatoriano")
    @UserMessage("Responde esta pregunta legal: {{pregunta}}")
    fun consultar(@V("pregunta") pregunta: String): String
    
    // Extraer datos estructurados
    @UserMessage("Extrae la información: {{texto}}")
    fun extraerPersona(@V("texto") texto: String): Persona
}

// Uso
val asistente = AiServices.create(AsistenteLegal::class.java, chatModel)
val respuesta = asistente.consultar("¿Cuál es el plazo de prescripción?")
val persona = asistente.extraerPersona("María tiene 30 años y vive en Quito")
```

**Lo que AiService hace por ti automáticamente:**
1. ✅ Construye los prompts
2. ✅ Llama al modelo
3. ✅ Parsea la respuesta (texto o JSON)
4. ✅ Maneja memoria (si le das ChatMemory)
5. ✅ Inyecta contexto de RAG (si le das ContentRetriever)
6. ✅ Convierte el texto a objetos (Persona, List, etc.)

---

## Tabla resumen

| Componente | Función | ¿Cuándo usarlo? |
|------------|---------|-----------------|
| **ChatLanguageModel** | Chat simple | Preguntas básicas |
| **EmbeddingModel** | Vectorizar texto | Búsqueda, RAG |
| **TokenCountEstimator** | Contar tokens | Control de costos/límites |
| **ChatMemory** | Recordar conversación | Chatbots con contexto |
| **StreamingChatLanguageModel** | Respuesta en tiempo real | Textos largos, mejor UX |
| **AiService** | Abstracción de alto nivel | Simplificar código |

---

## Flujo típico en una app RAG completa

```kotlin
// 1. ChatMemory - recuerda contexto
val memory = MessageWindowChatMemory.withMaxMessages(10)

// 2. StreamingChatLanguageModel - mejor experiencia
val streamingModel: StreamingChatLanguageModel = // ...

// 3. EmbeddingModel - para buscar documentos
val embeddingModel: EmbeddingModel = // ...

// 4. AiService - todo junto
interface RAGChatbot {
    fun chat(message: String): String
}

val bot = AiServices.builder(RAGChatbot::class.java)
    .chatLanguageModel(streamingModel)  // modelo de chat
    .chatMemory(memory)                  // memoria
    .contentRetriever(retriever)         // RAG (embeddings)
    .build()

// 5. TokenCountEstimator - verificar no exceder límites
val counter: TokenCountEstimator = // ...
if (counter.estimateTokenCount(prompt) > 4000) {
    // resumir o dividir
}
```

¿Te gustaría que profundice en alguno en particular o que te muestre un ejemplo completo combinando todos?