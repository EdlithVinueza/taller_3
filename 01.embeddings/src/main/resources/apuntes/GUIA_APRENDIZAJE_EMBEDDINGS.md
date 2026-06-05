# 📚 Guía de Aprendizaje: Embeddings (01.embeddings)

## Descripción General

Este módulo es un **tutorial interactivo sobre embeddings desde cero**. Sigue el pipeline completo:
- **Tokenización**: Texto → IDs
- **Muestreo**: IDs → Pares Input/Target  
- **Embeddings**: IDs → Vectores Densos (Significado)

---

## 🎯 Orden de Aprendizaje Recomendado

### **FASE 1: TOKENIZACIÓN (Texto → IDs)**

#### 📌 01_Pair.java
**¿QUÉ HACE?**
- Define estructura simple: `record Pair(int tokenId, String token)`
- Mapeo bidireccional: palabra ↔ ID

**¿POR QUÉ?**
- Los modelos necesitan números, no texto
- Cada palabra única debe tener ID único
- Pair permite mapeo rápido en ambas direcciones

**EJEMPLO:**
```
Pair(0, "<|endoftext|>")  → token fin
Pair(1, "the")            → palabra común
Pair(42, "hello")         → palabra ordinaria
Pair(100, "<|unk|>")      → palabra desconocida
```

**FIN:** Entender que vocabulario = lista de Pairs

---

#### 📌 02_SimpleTokenizerV1.java
**¿QUÉ HACE?**
- Tokenizador educativo básico: texto ↔ IDs
- Descarta palabras desconocidas (pierde información)

**¿POR QUÉ?**
- Versión simple para entender el concepto
- V1 = ignora palabras fuera de vocabulario
- Problema: pérdida de información

**PROCESO:**
1. Divide texto con regex
2. Busca cada palabra en mapa token→ID
3. Si no encuentra, descarta (retorna null)
4. Reconstruye quitando valores nulos

**SALIDA:**
```
encode("hello world"):   → [0, 1]
encode("hello xyz"):     → [0] (xyz se descarta)
```

**LIMITACIÓN:** Pierde palabras desconocidas

---

#### 📌 03_SimpleTokenizerV2.java
**¿QUÉ HACE?**
- Tokenizador mejorado: maneja palabras desconocidas
- Mapea desconocidas a token especial `<|unk|>`

**¿POR QUÉ?**
- V1 pierde información
- V2 preserva usando token especial
- Patrón usado por modelos reales

**MEJORA SOBRE V1:**
```
V1: "hello xyz" → [0] (xyz se pierde)
V2: "hello xyz" → [0, 100] (xyz → ID de <|unk|>)
```

**TOKENS ESPECIALES:**
- `<|endoftext|>` → marca fin de documento
- `<|unk|>` → palabra desconocida
- `<|pad|>` → relleno
- `<|cls|>` → inicio clasificación

---

#### 📌 04_TestTokenizacion.java
**¿QUÉ HACE?**
- Demostración completa ejecutable
- Construye vocabulario desde corpus
- Demuestra V1 y V2 lado a lado

**¿POR QUÉ?**
- Integra todo lo aprendido en 01-03
- Muestra pipeline real: archivo → vocab → tokenización

**PROCESO:**
1. Carga `the-verdict.txt`
2. Divide con regex
3. Obtiene vocabulario único
4. Asigna IDs secuenciales
5. Demo V1 y V2 con texto

**SALIDA:**
```
=== TOKENIZACIÓN V1 ===
[0] Pair[--: 0]
[1] Pair["": 1]
...
[50] Pair[you: 50]

Texto: "It's the last he painted..."
IDs: [123, 456, 789, ...]
Decodificado: It's the last he painted...
```

**FIN:** Ejecuta para ver tokenización en acción

---

#### 📌 05_TokkitTokenizer.java
**¿QUÉ HACE?**
- Usa tokenizador REAL (JTokkit, compatible OpenAI)
- Demuestra cómo funcionan tokenizadores en producción

**¿POR QUÉ?**
- SimpleV1/V2 son educativos, pero simplistas
- Modelos reales usan BPE (Byte Pair Encoding)
- JTokkit es Java port del tokenizer de GPT

**DIFERENCIAS:**
```
Educativo (V1/V2):
  - Split por regex simple
  - VocabSize: ~10K palabras
  - Tokens = palabras enteras

Real (JTokkit):
  - BPE (compresión)
  - VocabSize: ~50K tokens
  - Tokens = subpalabras (ej: "engineering" → "en" "gin" "eer" "ing")
```

**SALIDA:**
```
Texto: "Hello, do you like tea?"
Tokens: 18
IDs: [9906, 11, 656, 345, ...]
Decodificado: Hello, do you like tea?
```

**FIN:** Entender cómo funcionan tokenizadores reales

---

### **FASE 2: MUESTREO (IDs → Pares Input/Target)**

#### 📌 06_DatasetItem.java
**¿QUÉ HACE?**
- Contenedor: `record DatasetItem(List<Integer> input, List<Integer> target)`
- Estructura para entrenamiento supervisado

**¿POR QUÉ?**
- Entrenamiento necesita (entrada, salida esperada)
- Tarea: predecir siguiente token
- Input es target desplazado 1 paso

**ESTRUCTURA:**
```
Input:  [t1, t2, t3, t4]
Target: [t2, t3, t4, t5]  (corrido 1 paso)
```

**CONCEPTO:**
- Modelo aprende: "si ves [t1,t2,t3,t4] → predice [t2,t3,t4,t5]"
- Ambas listas: mismo tamaño
- Target = input con next token al final

---

#### 📌 07_DataSampling.java
**¿QUÉ HACE?**
- Genera dataset completo usando ventanas deslizantes
- Crea ~1000+ DatasetItem del corpus

**¿POR QUÉ?**
- No podemos procesar todo el corpus a la vez
- Dividimos en ventanas (context size)
- Cada ventana = un ejemplo de entrenamiento

**VENTANA DESLIZANTE:**
```
Secuencia: [1, 2, 3, 4, 5, 6, 7, 8]
Context Size = 4

Paso 1: input=[1,2,3,4],  target=[2,3,4,5]
Paso 2: input=[2,3,4,5],  target=[3,4,5,6]
Paso 3: input=[3,4,5,6],  target=[4,5,6,7]
...
```

**PROCESO:**
1. Leer corpus
2. Tokenizar con JTokkit
3. Crear ventanas deslizantes (loop por posición i)
4. Para cada ventana: crear DatasetItem
5. Guardar en List<DatasetItem>

**SALIDA:**
```
Dataset generado: 2500 pares input/target
Item 0: DatasetItem([45, 120, 89, 300], [120, 89, 300, 12])
Item 1: DatasetItem([120, 89, 300, 12], [89, 300, 12, 456])
```

**FIN:** Dataset listo para entrenar modelo

---

### **FASE 3: EMBEDDINGS (IDs → Vectores)**

#### 📌 08_EmbeddingTest.java
**¿QUÉ HACE?**
- Pipeline COMPLETO: tokenización → dataset → embeddings
- Demuestra embedding MANUAL vs embedding REAL

**¿POR QUÉ?**
- IDs son números discretos sin significado
- Embeddings = representación vectorial del SIGNIFICADO
- Dos palabras similares tienen embeddings similares

**DIFERENCIA IDs vs EMBEDDINGS:**
```
ID: 42
→ Solo un número, sin significado

Embedding: [-0.12, 0.45, -0.78, 0.01, ...]
→ Vector de 384 dimensiones
→ Captura significado, contexto, relaciones
```

**ESTRUCTURA EN 3 PARTES:**

**PARTE 1: Generar Dataset**
- Tokenización + ventanas deslizantes
- (Recapitulación de fases anteriores)

**PARTE 2: Embedding Manual (Educativo)**
- Usa DJL para crear matriz 50257×4
- Para cada token ID: obtén vector de 4 números
- Valores aleatorios (sin significado semántico)

**PARTE 3: Embedding Real (Producción)**
- Usa modelo preentrenado AllMiniLmL6V2
- 384 dimensiones
- Valores normalizados con significado real

**COMPARACIÓN:**
```
Manual:     4 dimensiones, números aleatorios [-1, 1]
Real:       384 dimensiones, representa SIGNIFICADO

El modelo real fue entrenado con millones de textos.
Cada dimensión captura aspecto semántico diferente.
```

**SALIDA:**
```
Manual Embedding:
Input indices: [45, 120, 89, 300]
Embedding shape: (4, 4)

Real Embedding:
Embedding size: 384 dimensiones
Primeros 10: [-0.123, 0.456, ...]
```

**FIN:** Entender la transformación completa: Texto → Números → Significado

---

## 📊 Flujo Visual Completo

```
┌──────────────────────────────────┐
│ ENTRADA: Texto (the-verdict.txt) │
└───────────────┬──────────────────┘
                ↓
    ┌───────────────────────┐
    │ FASE 1: TOKENIZACIÓN  │
    │ Texto → Vocabulario   │
    │ Vocabulario → IDs     │
    │                       │
    │ Archivos:            │
    │ • Pair.java          │
    │ • V1, V2, Real       │
    │ • TestTokenizacion   │
    └───────────┬───────────┘
                ↓
    ┌───────────────────────┐
    │ FASE 2: MUESTREO      │
    │ IDs → Ventanas        │
    │ Ventanas → Pares I/T  │
    │                       │
    │ Archivos:            │
    │ • DatasetItem.java   │
    │ • DataSampling.java  │
    └───────────┬───────────┘
                ↓
    ┌───────────────────────┐
    │ FASE 3: EMBEDDINGS    │
    │ IDs → Vectores        │
    │ Manual vs Real        │
    │                       │
    │ Archivos:            │
    │ • EmbeddingTest.java │
    └───────────┬───────────┘
                ↓
┌──────────────────────────────────┐
│ SALIDA: Embeddings (384 dims)    │
│ Listos para:                     │
│ ✓ Entrenar modelo               │
│ ✓ Búsqueda semántica            │
│ ✓ Similitud entre textos        │
│ ✓ Clustering                    │
└──────────────────────────────────┘
```

---

## 🔄 Conceptos Clave

| Concepto | Definición | Ejemplo |
|----------|-----------|---------|
| **Token** | Unidad mínima de texto (palabra, símbolo) | "hello", ".", "world" |
| **Vocabulario** | Conjunto de tokens únicos | ["hello", "world", ".", ...] |
| **Tokenización** | Convertir texto en IDs | "hello" → 42 |
| **Pair** | Estructura token ↔ ID | Pair(42, "hello") |
| **Embedding** | Vector que representa significado | [-0.12, 0.45, -0.78, ...] |
| **Ventana Deslizante** | Secuencia movimiento por corpus | [1,2,3,4], [2,3,4,5], ... |
| **Context Size** | Tamaño de ventana | 4, 8, 512, etc. |
| **BPE** | Byte Pair Encoding (compresión) | GPT tokenizer |

---

## 💡 Flujo Recomendado para Aprender

1. **Lee Pair.java** → Entiende estructura
2. **Ejecuta TestTokenizacion.main()** → Ve vocabulario
3. **Lee SimpleTokenizerV1** → Entiende encoding basic
4. **Lee SimpleTokenizerV2** → Entiende manejo <|unk|>
5. **Lee TokkitTokenizer** → Ve tokenizer real
6. **Ejecuta DataSampling.main()** → Genera dataset
7. **Lee DatasetItem** → Entiende estructura I/T
8. **Ejecuta EmbeddingTest.main()** → Ve pipeline completo

---

## 🚀 Próximos Pasos

### Después de Embeddings (01), aprende:
- **02.langchain4j**: Usar embeddings en aplicaciones
  - Búsqueda semántica
  - Chat con IA
  - Extracción de datos

- **03.spring-ai**: Integración con Spring Boot
  - REST APIs
  - Aplicaciones web

### Proyectos Prácticos:
1. **RAG (Retrieval Augmented Generation)**
   - Búsqueda + Chat
   - Pregunta sobre documentos

2. **Chatbot con Memoria**
   - Embeddings + Chat Memory

3. **Clasificador de Textos**
   - Embeddings + ML

---

## ⚙️ Configuración Técnica

**Dependencias principales:**
- `jtokkit`: Tokenizador real (OpenAI compatible)
- `djl`: Arrays/matrices para embeddings manual
- `langchain4j`: Framework de IA
- `langchain4j-embeddings`: Modelo AllMiniLm

**Corpus:**
- Archivo: `the-verdict.txt` (~20KB)
- Tamaño: ~500 tokens
- Propósito: Educativo, manejo fácil

---

## 📝 Checklist de Aprendizaje

- [ ] Entiendo qué es un token
- [ ] Puedo explicar diferencia V1 vs V2
- [ ] Conozco qué son tokens especiales
- [ ] Entiendo ventanas deslizantes
- [ ] Sé diferencia entre IDs y embeddings
- [ ] Ejecuté todos los programas
- [ ] Puedo explicar el flujo completo sin docs

---

## 🎓 Resumen

**Nivel 1 (Embeddings):**
- Texto → Números (Tokenización)
- Números → Pares (Muestreo)
- Pares → Vectores (Embeddings)
- Todo para entrenar/usar modelos de IA

**Próximo nivel (LangChain4j):**
- Usar embeddings en aplicaciones reales
- Combinar con chat, búsqueda, etc.

---

**¡Felicidades al completar 01.embeddings! 🎓**
