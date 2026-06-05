# Mapa Completo: 01.embeddings - Estructura y Propósito

## Árbol de Carpetas

```
01.embeddings/
├── src/main/java/com/programacion/taller3/
│   └── study/
│       ├── tokenization/       (FASE 1: Texto → IDs)
│       │   ├── Pair.java
│       │   ├── SimpleTokenizerV1.java
│       │   ├── SimpleTokenizerV2.java
│       │   ├── TestTokenizacion.java
│       │   └── TokkitTokenizer.java
│       │
│       ├── dataset/            (FASE 2: IDs → Pares Input/Target)
│       │   ├── DatasetItem.java
│       │   └── DataSampling.java
│       │
│       ├── embedding/          (FASE 3: IDs → Vectores Densos)
│       │   └── EmbeddingTest.java
│       │
│       └── teoria/             (DOCUMENTACIÓN)
│           ├── README.md                              ← ÍNDICE
│           ├── guia_interaccion_modelos_ia.md         ← INTRO
│           ├── paso_a_paso_detallado.md               ← TÉCNICA
│           └── por_que_cada_paso.md                   ← PROFUNDA
│
├── build.gradle.kts            (Dependencias del proyecto)
├── the-verdict.txt             (Corpus de ejemplo)
└── ... otros archivos Maven/Gradle
```

---

## Descripción por Archivo

### FASE 1: TOKENIZACIÓN

#### `tokenization/Pair.java`
```java
record Pair(int tokenId, String token)
```
- **Propósito**: Estructura simple token ↔ ID
- **Entrada**: N/A (es solo una estructura)
- **Salida**: Pares (id, palabra)
- **Uso**: Base del vocabulario
- **Comentarios**: Explica mapeo bidireccional

#### `tokenization/SimpleTokenizerV1.java`
```java
class SimpleTokenizerV1
  - encode(String text) → List<Integer>
  - decode(List<Integer> ids) → String
```
- **Propósito**: Codificador/decodificador didáctico (V1)
- **Entrada**: Texto + vocabulario
- **Salida**: IDs (ignora palabras desconocidas)
- **Uso**: Entender flujo básico
- **Comentarios**: Explica split, mapeo, stream

#### `tokenization/SimpleTokenizerV2.java`
```java
class SimpleTokenizerV2
  - encode(String text) → List<Integer>
  - decode(List<Integer> ids) → String
```
- **Propósito**: Codificador/decodificador mejorado (V2)
- **Entrada**: Texto + vocabulario (con `<|unk|>`)
- **Salida**: IDs (mapea desconocidos a `<|unk|>`)
- **Uso**: Manejar palabras fuera de vocabulario
- **Comentarios**: Explica getOrDefault con `<|unk|>`

#### `tokenization/TestTokenizacion.java`
```java
class TestTokenizacion
  - vocabulary(String fileName) → List<Pair>
  - vocabularyEx(String fileName) → List<Pair>
  - main() → demo ambos tokenizadores
```
- **Propósito**: Demo completa de tokenización
- **Entrada**: the-verdict.txt
- **Salida**: Vocabulario, códigos, decodificaciones
- **Uso**: Ejecutar para ver tokenización en acción
- **Comentarios**: Paso a paso del proceso

#### `tokenization/TokkitTokenizer.java`
```java
class TokkitTokenizer
  - main() → demo con tokenizador real
```
- **Propósito**: Usar tokenizador real (JTokkit)
- **Entrada**: Texto plano
- **Salida**: IDs usando encoding OpenAI
- **Uso**: Ver cómo lo hace un modelo real
- **Comentarios**: Explica registry, encoding, decode

---

### FASE 2: MUESTREO/DATASET

#### `dataset/DatasetItem.java`
```java
record DatasetItem(List<Integer> input, List<Integer> target)
```
- **Propósito**: Contenedor para pareja input/target
- **Entrada**: N/A (es solo estructura)
- **Salida**: Una pareja de ventanas
- **Uso**: Entrenar modelo (aprender predicción siguiente token)
- **Comentarios**: Explica input desplazado vs target

#### `dataset/DataSampling.java`
```java
class DataSampling
  - main() → crea dataset completo
```
- **Propósito**: Generar pares input/target desde corpus
- **Entrada**: the-verdict.txt
- **Salida**: List<DatasetItem> con ~1000s pares
- **Uso**: Preparar datos para entrenar
- **Comentarios**: Explica ventanas deslizantes, desplazamiento

---

### FASE 3: EMBEDDINGS

#### `embedding/EmbeddingTest.java`
```java
class EmbeddingTest
  - main() → demo embeddings manual + real
```
- **Propósito**: Mostrar cómo se convierten IDs a vectores
- **Entrada**: Dataset + texto
- **Salida**:
  - Parte 1: Matriz de embeddings (DJL) - manual
  - Parte 2: Vector denso (LangChain4j) - real
- **Uso**: Entender representación vectorial
- **Comentarios**: Explica NDArray, indexing, modelo real

---

### DOCUMENTACIÓN (TEORÍA)

#### `teoria/README.md`
- **Propósito**: Índice de documentación
- **Contenido**: Guía de lectura, flujo de aprendizaje, mapa código ↔ teoría
- **Lee primero**: Sí

#### `teoria/guia_interaccion_modelos_ia.md`
- **Propósito**: Introducción conceptual
- **Contenido**: Qué se hace en cada fase, por qué importa
- **Nivel**: Principiante

#### `teoria/paso_a_paso_detallado.md`
- **Propósito**: Desglose técnico fase por fase
- **Contenido**: 5 fases con ejemplos concretos de código
- **Nivel**: Intermedio

#### `teoria/por_que_cada_paso.md`
- **Propósito**: Razonamiento profundo detrás de cada decisión
- **Contenido**: Por qué tokenización, por qué embeddings, por qué funciona
- **Nivel**: Avanzado

---

## Flujo de Datos (Grande Visual)

```
┌──────────────────────────────────────────────────────────────┐
│ ENTRADA: Archivo de Texto (the-verdict.txt)                 │
└──────────────────────────────┬───────────────────────────────┘
                               ↓
        ┌──────────────────────────────────────────┐
        │ FASE 1: TOKENIZACIÓN                     │
        │ Archivo → Tokens → Vocabulario → IDs     │
        │                                          │
        │ Clases:                                  │
        │  • TestTokenizacion.vocabulary()          │
        │  • SimpleTokenizerV1/V2.encode()         │
        │  • TokkitTokenizer (real)                │
        └──────────────────────┬───────────────────┘
                               ↓
        ┌──────────────────────────────────────────┐
        │ FASE 2: MUESTREO                         │
        │ IDs → Ventanas Deslizantes → Pares I/T   │
        │                                          │
        │ Clases:                                  │
        │  • DataSampling (ventanas)               │
        │  • DatasetItem (estructura)              │
        └──────────────────────┬───────────────────┘
                               ↓
        ┌──────────────────────────────────────────┐
        │ FASE 3: EMBEDDINGS                       │
        │ IDs → Vectores Densos (significado)      │
        │                                          │
        │ Clases:                                  │
        │  • EmbeddingTest (manual + real)         │
        │  • DJL (manual) + LangChain4j (real)     │
        └──────────────────────┬───────────────────┘
                               ↓
┌──────────────────────────────────────────────────────────────┐
│ SALIDA: Embeddings Listos para:                             │
│  ✓ Entrenar un modelo (predicción siguiente token)          │
│  ✓ Búsqueda semántica (RAG)                                 │
│  ✓ Similitud entre textos                                   │
│  ✓ Clustering                                               │
└──────────────────────────────────────────────────────────────┘
```

---

## Dependencias del Proyecto

```gradle
jtokkit (1.1.0)           → Tokenizador real (OpenAI)
djl (0.36.0)              → Arrays/matrices (embeddings manual)
langchain4j (1.12.2)      → Framework IA
langchain4j-embeddings    → Modelo de embeddings (AllMiniLM)
pytorch-engine            → Backend DJL
```

---

## Cómo Ejecutar

### Opción 1: Gradle
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

### Opción 2: IDE
1. Abre proyecto en IntelliJ/Eclipse
2. Click derecho en cada `main()` → Run

---

## Checklist de Aprendizaje

- [ ] Leo `guia_interaccion_modelos_ia.md`
- [ ] Ejecuto `TestTokenizacion.main()`
- [ ] Entiendo Pair, SimpleTokenizerV1, SimpleTokenizerV2
- [ ] Leo `paso_a_paso_detallado.md` (FASE 1)
- [ ] Ejecuto `DataSampling.main()`
- [ ] Entiendo DatasetItem, ventanas deslizantes
- [ ] Leo `paso_a_paso_detallado.md` (FASE 2)
- [ ] Ejecuto `EmbeddingTest.main()`
- [ ] Entiendo embeddings manual vs real
- [ ] Leo `paso_a_paso_detallado.md` (FASE 3-5)
- [ ] Leo `por_que_cada_paso.md` completamente
- [ ] Puedo explicar flujo completo sin mirar documentación

---

## Notas Finales

- **Arquivos están comentados**: Cada clase `.java` tiene comentarios explicando cada sección
- **Documentación es escalable**: Empieza simple, va a profundo
- **Código es ejecutable**: No es pseudocódigo, es real y funciona
- **Corpus es pequeño**: `the-verdict.txt` es solo ~20KB, útil para propósitos educativos

