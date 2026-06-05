# Teoría: Cómo Funciona la Interacción con Modelos de IA

Esta carpeta contiene documentación completa sobre cada paso del pipeline de IA implementado en `01.embeddings`.

## Archivos de Teoría

### 1. **guia_interaccion_modelos_ia.md** (EMPEZAR AQUÍ)
Introducción de alto nivel.
- Qué es tokenización, dataset, embeddings
- Por qué cada paso es importante
- Cómo se conectan entre sí
- Flujo completo: texto → modelo

**Lectura**: 10 minutos

---

### 2. **paso_a_paso_detallado.md** (LUEGO LEE ESTO)
Desglose técnico paso por paso con ejemplos concretos.
- Fase 1: Tokenización (Pair, SimpleTokenizer, JTokkit)
- Fase 2: Muestreo (DatasetItem, DataSampling)
- Fase 3: Embeddings (manual con DJL, real con LangChain4j)
- Fase 4: Flujo integral y ejemplo RAG
- Fase 5: Por qué todo importa

**Lectura**: 30 minutos

---

### 3. **por_que_cada_paso.md** (PROFUNDIZA)
El razonamiento matemático y conceptual detrás de cada decisión.
- Por qué los modelos necesitan números (tokenización)
- Por qué creamos vocabularios (relaciones semánticas)
- Por qué muestreamos con ventanas (entrenamiento autoregresivo)
- Por qué embeddings son cruciales (similitud semántica)
- Por qué funciona el aprendizaje (gradualmente reduce error)
- RAG y búsqueda semántica (aplicación práctica)

**Lectura**: 25 minutos

---

## Flujo Recomendado de Aprendizaje

1. **Entiende el concepto** → Lee `guia_interaccion_modelos_ia.md`
2. **Ejecuta código** → Corre `TestTokenizacion.java`, `DataSampling.java`, `EmbeddingTest.java`
3. **Estudia pasos detallados** → Lee `paso_a_paso_detallado.md`
4. **Comprende el por qué** → Lee `por_que_cada_paso.md`
5. **Relaciona con código** → Mira comentarios en cada archivo `.java`

---

## Mapa Rápido: Código ↔ Teoría

| Código | Paso | En Detallado | En Por Qué |
|--------|------|-------------|-----------|
| `Pair.java` | Vocabulario | Paso 1.1 | Sección 1 |
| `SimpleTokenizerV1.java` | Codificación basica | Paso 1.3 | Sección 1 |
| `SimpleTokenizerV2.java` | Con token especial | Paso 1.4 | Sección 1 |
| `TokkitTokenizer.java` | Tokenizador real | Paso 1.5 | Sección 1 |
| `DatasetItem.java` | Estructura de par | Paso 2.3 | Sección 2 |
| `DataSampling.java` | Generación de dataset | Paso 2.2 | Sección 2 |
| `EmbeddingTest.java` | Embeddings | Paso 3 | Sección 3 |

---

## Conceptos Clave Para Recordar

### Tokenización
- Convierte **texto** → **números (IDs)**
- Necesario porque modelos son máquinas matemáticas
- Requiere vocabulario conocido

### Dataset
- Convierte **tokens** → **pares input/target**
- Estructura para entrenar predicción del siguiente token
- Ventanas deslizantes reutilizan datos

### Embeddings
- Convierte **IDs** → **vectores densos (significado)**
- Captura relaciones semánticas
- Permite búsqueda y similitud

### Flujo Completo
```
TEXTO DEL USUARIO
    ↓ (Tokenización)
SECUENCIA DE IDS
    ↓ (Embedding)
VECTORES DENSOS
    ↓ (Modelo)
PREDICCIÓN / BÚSQUEDA / CLASIFICACIÓN
    ↓ (Decodificación)
TEXTO DE RESPUESTA
```

---

## Preguntas Frecuentes

**¿Necesito memorizar todas las dimensiones?**
No. Lo importante es entender QUÉ hace cada paso y POR QUÉ.

**¿Todos los modelos usan estos pasos?**
Esencialmente sí. Detalles varían (tamaño vocabulario, contexto, arquitectura), pero el flujo es universal.

**¿Por qué hay 3 archivos de documentación?**
Porque hay 3 niveles de comprensión:
1. Alto nivel (qué se hace)
2. Medio nivel (cómo se implementa)
3. Profundo (por qué es así)

**¿Debo leerlos todos?**
Comienza con #1 y #2. Vuelve a #3 cuando quieras realmente entender.

---

## Siguientes Pasos (Fuera de Esta Carpeta)

Una vez domines `01.embeddings`:

- **02.langchain4j**: Cómo usar un modelo real con búsqueda semántica
- **03.spring-ai**: Cómo servir un LLM en una aplicación web

---

**Última actualización**: 2026-05-30
**Nivel**: Principiante → Intermedio
