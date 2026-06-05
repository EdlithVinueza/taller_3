## Guía Rápida para interactuar con los modelos

---

# GUÍA DE USO DE MODELOS LLAMA.CPP

## Modelo 1: Chat (Llama 3.2)
**Archivo:** `llama-3.2-1b-instruct-q8_0.gguf`  
**Endpoint:** `http://127.0.0.1:8080/v1/chat/completions`

### Desde Postman:

```
Método: POST
URL: http://127.0.0.1:8080/v1/chat/completions
Headers: Content-Type → application/json
Body (raw JSON):
```

```json
{
  "model": "llama",
  "messages": [
    {
      "role": "user",
      "content": "¿Qué es llama.cpp?"
    }
  ],
  "max_tokens": 256
}
```

### Desde el navegador (Consola F12):

```javascript
fetch('http://127.0.0.1:8080/v1/chat/completions', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    model: 'llama',
    messages: [{ role: 'user', content: '¿Qué es llama.cpp?' }],
    max_tokens: 256
  })
})
.then(res => res.json())
.then(data => console.log(data.choices[0].message.content));
```

### Desde terminal (curl):

```bash
curl -X POST http://127.0.0.1:8080/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d "{\"model\":\"llama\",\"messages\":[{\"role\":\"user\",\"content\":\"¿Qué es llama.cpp?\"}],\"max_tokens\":256}"
```

---

## Modelo 2: Embeddings (all-MiniLM)
**Archivo:** `all-minilm-l6-v2-q8_0.gguf`  
**Endpoint:** `http://127.0.0.1:8080/embedding`

### Desde Postman:

```
Método: POST
URL: http://127.0.0.1:8080/embedding
Headers: Content-Type → application/json
Body (raw JSON):
```

```json
{
  "content": "hola mundo"
}
```

### Desde el navegador (Consola F12):

```javascript
fetch('http://127.0.0.1:8080/embedding', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ content: 'hola mundo' })
})
.then(res => res.json())
.then(data => console.log(data));
```

### Desde terminal (curl):

```bash
curl -X POST http://127.0.0.1:8080/embedding \
  -H "Content-Type: application/json" \
  -d "{\"content\":\"hola mundo\"}"
```

---

## Modelo 3: Chat Alternativo (Gemma 4)
**Archivo:** `gemma-4-E4B-it-Q4_K_S.gguf`  
**Endpoint:** `http://127.0.0.1:8080/v1/chat/completions`

### Desde Postman (mismo que Llama 3.2):

```json
{
  "model": "gemma",
  "messages": [
    {
      "role": "user",
      "content": "¿Qué es la inteligencia artificial?"
    }
  ],
  "max_tokens": 256
}
```

### Desde el navegador (Consola F12):

```javascript
fetch('http://127.0.0.1:8080/v1/chat/completions', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    model: 'gemma',
    messages: [{ role: 'user', content: '¿Qué es la inteligencia artificial?' }],
    max_tokens: 256
  })
})
.then(res => res.json())
.then(data => console.log(data.choices[0].message.content));
```

---

## Cómo levantar cada modelo (desde CMD)

```cmd
cd C:\tools\llama-cpp

# Levantar Llama 3.2 (chat)
llama-server --model C:/Tools/llama-models/llama-3.2-1b-instruct-q8_0.gguf --port 8080

# Levantar all-MiniLM (embeddings)
llama-server --model C:/Tools/llama-models/all-minilm-l6-v2-q8_0.gguf --port 8080 --embedding

# Levantar Gemma 4 (chat)
llama-server --model C:/Tools/llama-models/gemma-4-E4B-it-Q4_K_S.gguf --port 8080
```

---

## Postman - Configuración paso a paso

### Para Chat (Llama o Gemma):

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://127.0.0.1:8080/v1/chat/completions` |
| **Headers** | `Content-Type: application/json` |
| **Body** | raw → JSON |

```json
{
  "model": "llama",
  "messages": [{"role": "user", "content": "Tu pregunta aquí"}],
  "max_tokens": 256,
  "temperature": 0.7
}
```

### Para Embeddings (all-MiniLM):

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://127.0.0.1:8080/embedding` |
| **Headers** | `Content-Type: application/json` |
| **Body** | raw → JSON |

```json
{
  "content": "Texto a vectorizar aquí"
}
```

---

## ¿Qué respuesta esperar?

### Chat (Llama/Gemma) → Respuesta de texto:
```json
{
  "choices": [{
    "message": {
      "content": "llama.cpp es una biblioteca para ejecutar modelos de lenguaje..."
    }
  }]
}
```

### Embeddings (all-MiniLM) → Vector numérico:
```json
[
  0.026735197752714157,
  -0.030073389410972595,
  0.11478076130151749,
  ...
]
```

---

## Notas importantes:

1. **Solo un modelo a la vez** en el puerto 8080
2. El navegador **NO** puede hacer POST desde la barra de direcciones (usa la consola F12)
3. `all-MiniLM` **NO responde preguntas**, solo genera números
4. `Llama` y `Gemma` **NO generan embeddings**, solo texto

---
