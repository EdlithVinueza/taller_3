# Paso a Paso Detallado: Cómo se Interactúa con Modelos de IA

## Introducción
Este documento desglosa exactamente qué sucede en cada fase cuando trabajas con modelos de IA. Usa como base el código en las carpetas `tokenization/`, `dataset/` y `embedding/`.

---

## FASE 1: TOKENIZACIÓN - Texto → Números

### ¿Por qué es importante?
Los modelos de IA NO entienden texto plano. Entienden números. Todo debe convertirse a una secuencia numérica.

### Paso 1.1: Crear el Vocabulario
**Archivo**: `tokenization/TestTokenizacion.java` → método `vocabulary()`

```
ENTRADA: Archivo de texto (the-verdict.txt)
         "It's the last he painted, you know..."

PASO 1: Leer archivo completo
PASO 2: Dividir en tokens con regex (palabras, signos, espacios)
        ["It", "'s", "the", "last", "he", "painted", ...]

PASO 3: Obtener único + ordenado
        ["!", "'s", "It", "a", "and", "be", ..., "you", "your", "yourself"]

PASO 4: Crear mapeo: token → id
        Pair(0, "!")
        Pair(1, "'s")
        Pair(2, "It")
        ...
        Pair(1000, "yourself")

SALIDA: List<Pair> vocab
```

Este es el vocabulario básico sin tokens especiales.

### Paso 1.2: Vocabulario Extendido (Modelo Real)
**Archivo**: `tokenization/TestTokenizacion.java` → método `vocabularyEx()`

Agrega dos tokens especiales que los modelos reales usan:
```
<|endoftext|>  → marca fin de texto
<|unk|>        → marca token desconocido
```

```
ENTRADA: Mismo archivo

PASOS 1-3: Idéntico al vocabulario básico

PASO 4: Agregar tokens especiales
        allWords.add("<|endoftext|>")
        allWords.add("<|unk|>")

SALIDA: List<Pair> vocabEx (con 2 tokens adicionales)
```

### Paso 1.3: Tokenizador Simple V1
**Archivo**: `tokenization/SimpleTokenizerV1.java`

```
ENTRADA: 
  - vocabulario (List<Pair>)
  - texto a codificar: "hello world"

FLUJO:
1. Crea dos mapas rápidos:
   strToInt: {"hello" → 5, "world" → 200, ...}
   intToStr: {5 → "hello", 200 → "world", ...}

2. Codificación (encode):
   "hello world" → split regex → ["hello", "world"]
                → busca en strToInt → [5, 200]
   SALIDA: [5, 200]

3. Decodificación (decode):
   [5, 200] → busca en intToStr → ["hello", "world"]
           → join con espacio → "hello world"
   SALIDA: "hello world"
```

**Características**:
- Si una palabra NO está en el vocabulario → se ignora (retorna null)
- Útil para análisis o depuración

### Paso 1.4: Tokenizador Simple V2
**Archivo**: `tokenization/SimpleTokenizerV2.java`

Mejora sobre V1: maneja palabras desconocidas con un token especial.

```
ENTRADA:
  - vocabulario con <|unk|>
  - texto con palabra desconocida: "hello unknownword world"

FLUJO (encode):
1. Split: ["hello", "unknownword", "world"]
2. Para "hello": existe → id = 5
   Para "unknownword": NO existe → retorna strToInt.get("<|unk|>") = 99
   Para "world": existe → id = 200
   SALIDA: [5, 99, 200]

VENTAJA: No se pierden datos; se marca desconocido explícitamente.
```

Este es más cercano a cómo funcionan tokenizadores reales.

### Paso 1.5: Tokenizador Real (JTokkit)
**Archivo**: `tokenization/TokkitTokenizer.java`

```
ENTRADA:
  - texto: "Hello, do you like tea?"

FLUJO:
1. Accede a registro de encodings reales (OpenAI models)
2. Selecciona encoding para TEXT_DAVINCI_003
3. Codifica: "Hello, do you like tea?"
              → [Hello, comma, space, do, ...]
              → [123, 45, 18, 901, ...]
   SALIDA: IntArrayList con ids reales del modelo

4. Decodifica de vuelta para verificar

VENTAJA: Usa el EXACTO encoding del modelo (GPT-3, etc.)
```

---

## FASE 2: MUESTREO DE DATOS - Preparar Pares Input/Target

### ¿Por qué es importante?
Para entrenar un modelo, necesitas PARES: "dado esto, predice aquello".
La forma estándar es usar ventanas deslizantes.

### Paso 2.1: Concepto Visual
```
Tokens del corpus: [t1, t2, t3, t4, t5, t6, t7, t8, t9, ...]

Ventana de tamaño 4 (contextSize = 4):

Iteración 1:
  input:  [t1, t2, t3, t4]
  target: [t2, t3, t4, t5]  ← desplazado 1

Iteración 2:
  input:  [t2, t3, t4, t5]
  target: [t3, t4, t5, t6]  ← desplazado 1

Iteración 3:
  input:  [t3, t4, t5, t6]
  target: [t4, t5, t6, t7]  ← desplazado 1

... y así sucesivamente
```

El modelo aprende: "si ves [t1, t2, t3, t4], el próximo es [t2, t3, t4, t5]".

### Paso 2.2: Implementación
**Archivo**: `dataset/DataSampling.java`

```
ENTRADA:
  - Archivo tokenizado (the-verdict.txt)
  - contextSize = 4

FLUJO:
1. Leer archivo completo
2. Tokenizar con JTokkit
   "It's the last..." → [123, 456, 789, ...]  (N tokens)

3. Crear ventanas deslizantes:
   for i = 0 to N-4:
     inputChunk = tokens[i:i+4]
     targetChunk = tokens[i+1:i+5]
     dataset.add(DatasetItem(inputChunk, targetChunk))

4. Resultado:
   dataset.size() = N - 4 (aproximadamente 1000s de pares)

EJEMPLO CONCRETO:
  Corpus tokenizado: [1, 5, 23, 45, 67, 89, 12, ...]
  
  Pair 1: input=[1, 5, 23, 45]  target=[5, 23, 45, 67]
  Pair 2: input=[5, 23, 45, 67] target=[23, 45, 67, 89]
  Pair 3: input=[23, 45, 67, 89] target=[45, 67, 89, 12]
  ...
```

### Paso 2.3: Estructura DatasetItem
**Archivo**: `dataset/DatasetItem.java`

```java
record DatasetItem(List<Integer> input, List<Integer> target)
```

Simple: cada item es un contenedor para una pareja input/target.

```
EJEMPLO:
  DatasetItem(
    input: [1, 5, 23, 45],
    target: [5, 23, 45, 67]
  )
```

---

## FASE 3: EMBEDDINGS - Vectores para Significado

### ¿Por qué es importante?
Los ids numéricos (123, 456, 789) no "significan nada" por sí solos.
Los embeddings son vectores DENSOS que capturan SIGNIFICADO.

```
ID del token: 123 (una dimensión)
vs.
Embedding: [0.5, -0.2, 0.9, ..., 0.1]  (384 dimensiones, por ejemplo)
```

Con embeddings puedes medir similitud semántica:
- "gato" ≈ "felino" (distancia cercana)
- "gato" ≠ "coche" (distancia lejana)

### Paso 3.1: Embedding Manual (Didáctico)
**Archivo**: `embedding/EmbeddingTest.java` → Primera parte

```
ENTRADA:
  - Dataset de pares input/target
  - Tamaño de vocabulario: 50257
  - Dimensión de embedding: 4

FLUJO:
1. Crear matriz de pesos (50257 x 4):
   
   weights[0] = [0.1, -0.3, 0.5, 0.2]   ← embedding del token 0
   weights[1] = [0.2, 0.1, -0.1, 0.8]   ← embedding del token 1
   ...
   weights[50256] = [-0.5, 0.6, 0.1, -0.2]

2. Para obtener embedding de tokens [1, 5, 23]:
   embedding = weights.get([1, 5, 23])
   
   Resultado: matriz 3x4 (3 tokens, 4 dimensiones cada uno)
   
   [0.2, 0.1, -0.1, 0.8]     ← embedding token 1
   [0.1, 0.5, 0.2, -0.3]     ← embedding token 5
   [0.0, -0.2, 0.9, 0.1]     ← embedding token 23

SALIDA: NDArray con forma (3, 4)
```

### Paso 3.2: Embedding Real (Modelo Preentrenado)
**Archivo**: `embedding/EmbeddingTest.java` → Segunda parte

```
ENTRADA:
  - Texto simple: "hello"

FLUJO:
1. Usar modelo preentrenado (AllMiniLmL6V2QuantizedEmbeddingModel)
   
   EmbeddingModel model = new AllMiniLmL6V2QuantizedEmbeddingModel()

2. Obtener embedding:
   
   Response<Embedding> response = model.embed("hello")
   float[] vector = response.content().vector()
   
   Resultado: float[] de tamaño 384
   [0.123, -0.456, 0.789, ..., 0.001]

VENTAJA: Estos embeddings ya capturan SIGNIFICADO de verdad.
         Se entrenaron en corpus masivo.
```

---

## FASE 4: CÓMO SALE TODO JUNTO - Flujo Integral

### Secuencia Real de Trabajo

```
┌─────────────────────────────────────────────────────┐
│ 1. USUARIO DA TEXTO: "What is machine learning?"   │
└─────────────────┬───────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────────────────┐
│ 2. TOKENIZACIÓN: texto → ids                        │
│    "What is machine learning?"                      │
│    → [345, 12, 7823, 5421]                          │
└─────────────────┬───────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────────────────┐
│ 3. EMBEDDING: ids → vectores                        │
│    [345, 12, 7823, 5421]                            │
│    → 4 vectores de 384 dims cada uno                │
└─────────────────┬───────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────────────────┐
│ 4. MODELO PROCESA VECTORES                          │
│    - Busca contexto relevante (RAG)                 │
│    - Genera respuesta (LLM)                         │
│    - Mide similitud (búsqueda semántica)            │
└─────────────────┬───────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────────────────┐
│ 5. SALIDA: "Machine learning is a subset of AI..." │
└─────────────────────────────────────────────────────┘
```

### Ejemplo Práctico: Búsqueda Semántica (RAG)

```
Pregunta: "¿Cómo funcionan los transformers?"

PASO 1 (Tokenización):
  "¿Cómo funcionan los transformers?"
  → [100, 200, 300, 400, 500]

PASO 2 (Embedding):
  [100, 200, 300, 400, 500]
  → query_vector = [0.5, -0.2, ..., 0.8]  (384 dims)

PASO 3 (Base de documentos embedida):
  Documentos previos también embedidos:
  doc1_vector = [0.4, -0.1, ..., 0.9]
  doc2_vector = [0.1, 0.5, ..., 0.2]
  doc3_vector = [0.6, -0.3, ..., 0.7]

PASO 4 (Similitud - coseno):
  similitud(query, doc1) = 0.92  ← MÁS SIMILAR
  similitud(query, doc2) = 0.45
  similitud(query, doc3) = 0.88

PASO 5 (Usar doc1 en contexto):
  Enviar a LLM:
    "Contexto: [contenido de doc1]
     Pregunta: ¿Cómo funcionan los transformers?"
  
  LLM genera respuesta específica basada en el contexto.

SALIDA: Respuesta precisa y contextualizada
```

---

## FASE 5: Por Qué Todo Esto Importa

### Para Entrenar un Modelo
- Necesitas datos tokenizados
- Necesitas pares input/target (FASE 2)
- El modelo aprende a predecir el siguiente token
- Usa embeddings internamente para representar significado

### Para Usar un Modelo (Inferencia/Producción)
- Tokenizas la entrada del usuario
- Conviertes a embeddings
- El modelo consulta bases de datos (RAG)
- Genera respuesta coherente

### Para Tareas Específicas
- **Similitud semántica**: Comparas embeddings (distancia)
- **Clustering**: Agrupas textos por proximidad de embeddings
- **Clasificación**: Entrenaruna capa simple sobre embeddings
- **Búsqueda**: Indexas embeddings en base de datos vectorial

---

## Resumen de Archivos y Qué Hacer Con Ellos

| Fase | Archivo | Qué Hace |
|------|---------|----------|
| **Tokenización** | `tokenization/TestTokenizacion.java` | Crea vocabulario |
| | `tokenization/SimpleTokenizerV1.java` | Codifica/decodifica (V1) |
| | `tokenization/SimpleTokenizerV2.java` | Codifica/decodifica (V2) |
| | `tokenization/TokkitTokenizer.java` | Usa tokenizador real |
| **Dataset** | `dataset/DataSampling.java` | Crea pares input/target |
| | `dataset/DatasetItem.java` | Estructura del par |
| **Embedding** | `embedding/EmbeddingTest.java` | Genera embeddings |

---

## Paso Final: Ejecución Real

1. Ejecuta `TestTokenizacion.main()` → verifica tokenización
2. Ejecuta `DataSampling.main()` → verifica pares generados
3. Ejecuta `EmbeddingTest.main()` → verifica embeddings

Cada uno te muestra la transformación de datos en esa fase.

