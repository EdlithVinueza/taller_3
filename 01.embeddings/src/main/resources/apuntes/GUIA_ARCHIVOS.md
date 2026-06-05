# 📚 Guía Rápida de Archivos - 01.embeddings

## Estructura de Aprendizaje

Este módulo implementa el **pipeline completo de embeddings**:
**Texto → Tokenización → Dataset → Embeddings**

---

## FASE 1: TOKENIZACIÓN (Texto → IDs)

### 📌 01_Pair.java
**Ubicación:** `src/main/java/com/programacion/taller3/study/tokenization/`

**¿Qué hace?**
- Estructura simple: `record Pair(int tokenId, String token)`
- Mapeo bidireccional: palabra ↔ ID

**Concepto clave:** 
```
Vocabulario = List<Pair>
Pair(0, "<|endoftext|>")
Pair(1, "the")
Pair(42, "hello")
```

**Salida:** 
```
Pair[the: 1]
Pair[hello: 42]
```

---

### 📌 02_SimpleTokenizerV1.java
**Ubicación:** `src/main/java/com/programacion/taller3/study/tokenization/`

**¿Qué hace?**
- Tokenizador educativo básico
- Convierte texto ↔ IDs
- **Descarta palabras desconocidas** (V1 limitación)

**Concepto clave:**
```
encode("hello world"):   → [0, 1]
encode("hello xyz"):     → [0] (xyz se descarta)
decode([0, 1]):          → "hello world"
```

**Métodos:**
- `encode(String)` → List<Integer> (pierde desconocidos)
- `decode(List<Integer>)` → String

**Limitación:** Pierde información (xyz no aparece)

---

### 📌 03_SimpleTokenizerV2.java
**Ubicación:** `src/main/java/com/programacion/taller3/study/tokenization/`

**¿Qué hace?**
- Versión mejorada de V1
- Maneja palabras desconocidas
- Mapea desconocidas a token especial `<|unk|>`

**Mejora sobre V1:**
```
V1: "hello xyz" → [0] (xyz se pierde)
V2: "hello xyz" → [0, 100] (xyz → <|unk|>)
```

**Métodos:**
- `encode(String)` → List<Integer> (preserva con <|unk|>)
- `decode(List<Integer>)` → String

**Ventaja:** No pierde información

---

### 📌 04_TestTokenizacion.java
**Ubicación:** `src/main/java/com/programacion/taller3/study/tokenization/`

**¿Qué hace?**
- Demostración completa ejecutable
- Construye vocabulario desde corpus
- Demuestra V1 y V2 lado a lado
- Lee: `the-verdict.txt`

**Métodos estáticos:**
- `vocabulary(fileName)` → vocabulario sin tokens especiales
- `vocabularyEx(fileName)` → vocabulario con <|endoftext|>, <|unk|>

**Proceso:**
1. Lee archivo
2. Tokeniza con regex
3. Obtiene únicos ordenados
4. Asigna IDs secuenciales

**Salida:**
```
=== TOKENIZACIÓN V1 ===
Pair[--: 0]
Pair["": 1]
...
IDs: [123, 456, 789, ...]
Decodificado: It's the last he painted...
```

**Ejecutar para:** Ver tokenización en acción

---

### 📌 05_TokkitTokenizer.java
**Ubicación:** `src/main/java/com/programacion/taller3/study/tokenization/`

**¿Qué hace?**
- Usa tokenizador REAL (JTokkit)
- Compatible con OpenAI
- Implementa BPE (Byte Pair Encoding)

**Diferencias con V1/V2:**
```
Educativo (V1/V2):
  - Regex simple
  - ~10K vocabulario

Real (JTokkit):
  - BPE (compresión)
  - ~50K tokens
  - Subpalabras: "engineering" → "en" "gin" "eer" "ing"
```

**Métodos:**
- `encodeOrdinary(text)` → IntArrayList de IDs
- `decode(ids)` → String

**Salida:**
```
Tokens: 18
IDs: [9906, 11, 656, 345, ...]
Decodificado: Hello, do you like tea?
```

**Ejecutar para:** Ver tokenizador real

---

## FASE 2: MUESTREO (IDs → Pares Input/Target)

### 📌 06_DatasetItem.java
**Ubicación:** `src/main/java/com/programacion/taller3/study/dataset/`

**¿Qué hace?**
- Contenedor: `record DatasetItem(List<Integer> input, List<Integer> target)`
- Estructura para entrenamiento supervisado

**Concepto clave:**
```
Input:  [t1, t2, t3, t4]
Target: [t2, t3, t4, t5]  (corrido 1 paso)

Modelo aprende: "si ves Input → predice Target"
```

**Tamaños:** input.size() == target.size()

**Usa:** DataSampling.java para generar muchos

---

### 📌 07_DataSampling.java
**Ubicación:** `src/main/java/com/programacion/taller3/study/dataset/`

**¿Qué hace?**
- Genera dataset completo
- Usa ventanas deslizantes
- Crea ~2500 DatasetItem

**Ventana Deslizante:**
```
Secuencia: [1, 2, 3, 4, 5, 6, 7, 8]
Context Size = 4

Paso 1: input=[1,2,3,4],  target=[2,3,4,5]
Paso 2: input=[2,3,4,5],  target=[3,4,5,6]
Paso 3: input=[3,4,5,6],  target=[4,5,6,7]
...
```

**Proceso:**
1. Lee corpus: `the-verdict.txt`
2. Tokeniza con JTokkit
3. Crea ventanas: for i in range(size - context):
4. Genera DatasetItem(input[i:i+context], input[i+1:i+context+1])
5. Guarda en List<DatasetItem>

**Salida:**
```
Total de tokens: 500
Dataset generado: 2500 pares input/target
Item 0: input=[45, 120, 89, 300], target=[120, 89, 300, 12]
Item 1: input=[120, 89, 300, 12], target=[89, 300, 12, 456]
```

**Ejecutar para:** Generar dataset de entrenamiento

---

## FASE 3: EMBEDDINGS (IDs → Vectores)

### 📌 08_EmbeddingTest.java
**Ubicación:** `src/main/java/com/programacion/taller3/study/embedding/`

**¿Qué hace?**
- Pipeline COMPLETO en un archivo
- Tokenización + Dataset + Embeddings
- Demuestra 2 enfoques: manual vs real

**¿POR QUÉ?**
```
ID: 42 (solo número, sin significado)
  ↓
Embedding: [-0.12, 0.45, -0.78, ...] (384 valores)
  ↓
Captura SIGNIFICADO, contexto, relaciones
```

**Estructura en 3 Partes:**

**PARTE 1: Generar Dataset**
- Tokenización + ventanas (recapitulación)

**PARTE 2: Embedding Manual (Educativo)**
- Usa DJL para crear matriz 50257×4
- Valores aleatorios (sin significado real)
- Propósito: entender estructura

**PARTE 3: Embedding Real (Producción)**
- Modelo preentrenado: AllMiniLmL6V2
- 384 dimensiones
- Valores con significado semántico

**Comparación:**
```
Manual:     4 dims, números random [-1, 1]
Real:       384 dims, representa SIGNIFICADO

Real model entrenado con millones de textos.
Cada dimensión = aspecto semántico diferente.
```

**Salida:**
```
Total de tokens: 500
Dataset creado: 2500 items

PARTE 2 - Manual:
Input indices: [45, 120, 89, 300]
Embedding shape: (4, 4)

PARTE 3 - Real:
Embedding size: 384 dimensiones
Primeros 10: [-0.123, 0.456, ...]
```

**Ejecutar para:** Ver pipeline completo

---

## 📊 Tabla Comparativa

| Archivo | Concepto | Complejidad | Ejecutable |
|---------|----------|-------------|-----------|
| Pair | Estructura Token-ID | ⭐ | No (es solo dato) |
| SimpleTokenizerV1 | Encoding básico | ⭐ | Sí (via TestTokenizacion) |
| SimpleTokenizerV2 | Encoding mejorado | ⭐ | Sí (via TestTokenizacion) |
| TestTokenizacion | Demo tokenización | ⭐⭐ | **SÍ** |
| TokkitTokenizer | Tokenizador real | ⭐⭐ | **SÍ** |
| DatasetItem | Estructura I/T | ⭐ | No (es solo dato) |
| DataSampling | Generar dataset | ⭐⭐⭐ | **SÍ** |
| EmbeddingTest | Pipeline completo | ⭐⭐⭐⭐ | **SÍ** |

---

## 🚀 Orden de Ejecución Recomendado

1. **TestTokenizacion.main()** → Entiende vocabulario y tokenización
2. **TokkitTokenizer.main()** → Ve tokenizador real
3. **DataSampling.main()** → Genera dataset
4. **EmbeddingTest.main()** → Ve pipeline completo

---

## 💻 Cómo Ejecutar

### Opción 1: Terminal (Gradle)
```bash
# Compilar
./gradlew :01.embeddings:build

# Ejecutar TestTokenizacion
./gradlew :01.embeddings:run --args="tokenization.com.programacion.embedding.TestTokenizacion"

# Ejecutar DataSampling
./gradlew :01.embeddings:run --args="dataset.com.programacion.embedding.DataSampling"

# Ejecutar EmbeddingTest
./gradlew :01.embeddings:run --args="embedding.com.programacion.embedding.EmbeddingTest"
```

### Opción 2: IDE (IntelliJ/Eclipse)
1. Abre proyecto en IDE
2. Navega a cada `main()` method
3. Click derecho → Run

---

## 📝 Notas Importantes

- **Archivo corpus:** `the-verdict.txt` (~20KB, ~500 tokens)
- **Context size (por defecto):** 4 tokens
- **AllMiniLmL6V2:** Modelo de embeddings eficiente, 384 dimensiones
- **JTokkit:** Compatible con GPT tokenizer de OpenAI
- **DJL:** Deep Java Library para operaciones matriciales

---

## 🔍 Debugging

Si algo falla:

1. **"File not found":** Verifica ruta `the-verdict.txt`
2. **"No main method":** Asegúrate de ejecutar archivo con `main()`
3. **OutOfMemory:** Reduce `context size` o tamaño del dataset
4. **Embedding size 0:** Verifica instalación AllMiniLm model

---

## 🎓 Resumen Visual

```
ENTRADA: the-verdict.txt
    ↓
TestTokenizacion
    ↓ Genera: Pair, Vocabulary
    ↓
TokkitTokenizer
    ↓ Genera: List<Integer> IDs
    ↓
DataSampling
    ↓ Genera: List<DatasetItem>
    ↓
EmbeddingTest
    ↓ Genera: float[] vectors (384 dims)
    ↓
SALIDA: Embeddings listos para usar
```

---

## 🌱 Próximos Pasos

### Después de dominar 01.embeddings:
1. Aprende **02.langchain4j**
   - Busca semántica con embeddings
   - Chat + Memory + Tools
   - Structured output

2. Integra con **03.spring-ai**
   - REST APIs
   - Aplicaciones web

### Proyectos:
- RAG: búsqueda + chat
- Chatbot: memoria + herramientas
- Clasificador: embeddings + ML

---

**¡Felicidades completando 01.embeddings! 🎓**
