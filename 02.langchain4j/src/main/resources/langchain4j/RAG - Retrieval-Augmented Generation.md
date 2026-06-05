## RAG (Retrieval-Augmented Generation)

**RAG** significa **Generación Aumentada por Recuperación** (Retrieval-Augmented Generation).

Es una técnica que mejora las respuestas de un LLM dándole **información adicional relevante** antes de que responda.

---

## Problema que resuelve RAG

Los LLM (como Llama o Gemma) tienen una limitación importante:

| Problema | Ejemplo |
|----------|---------|
| **Conocimiento limitado** | Solo saben hasta la fecha en que fueron entrenados |
| **No conocen tus documentos** | No saben qué hay en tus PDFs, correos, o base de datos |
| **Alucinaciones** | Inventan respuestas cuando no saben algo |

---

## ¿Cómo funciona RAG?

```
Pregunta del usuario: "¿Cuánto cuesta el producto X?"

PASO 1: Buscar información relevante
────────────────────────────────────
Tu pregunta → all-MiniLM (embeddings) → vector
                ↓
Buscar en tu base de documentos el vector más parecido
                ↓
Documento encontrado: "El producto X cuesta $100"

PASO 2: Aumentar la pregunta con esa información
────────────────────────────────────────────────────
Prompt final:
"Contexto: El producto X cuesta $100
Pregunta: ¿Cuánto cuesta el producto X?"

PASO 3: LLM genera respuesta informada
────────────────────────────────────────
Respuesta: "Según la información, el producto X cuesta $100"
```

---

## Diagrama visual

```
┌─────────────────────────────────────────────────────────────┐
│                       RAG COMPLETO                          │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Usuario: "¿Qué dice el contrato sobre plazos?"             │
│         │                                                    │
│         ▼                                                    │
│  ┌──────────────┐                                           │
│  │ all-MiniLM   │ → Convierte pregunta a vector             │
│  │ (embeddings) │                                           │
│  └──────────────┘                                           │
│         │                                                    │
│         ▼                                                    │
│  ┌──────────────┐                                           │
│  │ Vector Store │ → Busca documentos similares              │
│  │ (base datos) │   Encuentra: "Plazo 30 días"              │
│  └──────────────┘                                           │
│         │                                                    │
│         ▼                                                    │
│  ┌──────────────┐                                           │
│  │   Llama 3.2  │ → Responde con el contexto encontrado     │
│  │    (LLM)     │   "El plazo es de 30 días"                │
│  └──────────────┘                                           │
│         │                                                    │
│         ▼                                                    │
│  Respuesta al usuario                                        │
└─────────────────────────────────────────────────────────────┘
```

---

## Con tus modelos, un RAG sería:

### Paso 1: Embeddings (all-MiniLM)
```bash
# Convertir pregunta a vector
POST http://localhost:8080/embedding
{"content": "¿Cuál es el plazo de prescripción?"}

# Respuesta: [0.026, -0.030, 0.114, ...] (vector)
```

### Paso 2: Buscar en tu base de vectores
```python
# Encontrar los documentos con vectores más parecidos
documentos_relevantes = buscar_similares(vector_pregunta, top_k=3)
# Resultado: ["Plazo prescripción: 30 días", ...]
```

### Paso 3: Chat con contexto (Llama 3.2)
```bash
POST http://localhost:8080/v1/chat/completions
{
  "messages": [
    {"role": "system", "content": "Contexto: Plazo prescripción: 30 días"},
    {"role": "user", "content": "¿Cuál es el plazo de prescripción?"}
  ]
}

# Respuesta: "Según la información proporcionada, el plazo es de 30 días"
```

---

## Sin RAG vs Con RAG

| | Sin RAG | Con RAG |
|--|---------|---------|
| **Respuesta** | "No lo sé" o inventa | Responde con información real |
| **Fuentes** | Solo su entrenamiento | Tus documentos específicos |
| **Actualización** | Necesita reentrenamiento | Solo añadir documentos nuevos |
| **Alucinaciones** | Frecuentes | Reducidas drásticamente |

---

## Casos de uso típicos de RAG

- 📄 **Chat con tus PDFs** (facturas, contratos, informes)
- 📧 **Búsqueda en correos electrónicos**
- 💬 **Soporte técnico** (manuales/documentación)
- 🏥 **Historiales médicos**
- 📚 **Estudio de leyes/normativas**

---

## Resumen corto

> **RAG** = Buscar información relevante **antes** de que el LLM responda, para que responda con datos reales y no invente.

```
Embedding (buscar) + LLM (responder) = RAG
all-MiniLM      + Llama 3.2      = RAG
```

