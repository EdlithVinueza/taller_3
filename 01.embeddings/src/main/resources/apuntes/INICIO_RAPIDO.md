# 🚀 INICIO RÁPIDO - 01.embeddings

## En 5 Minutos

### ¿Qué hay aquí?
Una lección completa sobre **cómo funciona la IA internamente**:
- Texto → Números (Tokenización)
- Números → Pares de entrenamiento (Dataset)
- Pares → Vectores significativos (Embeddings)

### ¿Cómo empiezo?

#### Opción A: Leer Documentación (Recomendado)
```
1. Abre: 01.embeddings/src/main/java/com/programacion/taller3/study/teoria/README.md
2. Sigue el flujo de aprendizaje propuesto
3. Lee archivos .md en orden
```

#### Opción B: Ejecutar Código
```bash
# Terminal en raíz del proyecto
./gradlew :01.embeddings:build

# Ejecutar ejemplos
java -cp "01.embeddings/build/classes/java/main:..." tokenization.com.programacion.embedding.TestTokenizacion
java -cp "01.embeddings/build/classes/java/main:..." dataset.com.programacion.embedding.DataSampling
java -cp "01.embeddings/build/classes/java/main:..." embedding.com.programacion.embedding.EmbeddingTest
```

#### Opción C: IDE (Más Fácil)
```
1. Abre proyecto en IntelliJ/Eclipse
2. Navega a: src/main/java/com/programacion/taller3/study/
3. Click derecho en main() de cada clase → Run
```

---

## 📖 Los 3 Documentos Clave

### `teoria/guia_interaccion_modelos_ia.md`
**¿Qué?** Visión general de cada fase
**Lectura**: 10 min
**Para**: Principiantes

### `teoria/paso_a_paso_detallado.md`
**¿Cómo?** Implementación técnica
**Lectura**: 30 min
**Para**: Intermedios

### `teoria/por_que_cada_paso.md`
**¿Por qué?** Razonamiento profundo
**Lectura**: 25 min
**Para**: Avanzados

---

## 🎯 El Flujo en 1 Párrafo

Los modelos de IA son máquinas matemáticas que no entienden texto. Necesitan convertir texto a números (tokenización), luego aprender de pares input/target (muestreo), y finalmente representar significado como vectores densos (embeddings). Este módulo te muestra cada paso con código real.

---

## 📁 Estructura (Simplificada)

```
01.embeddings/
│
├── src/main/java/.../study/
│   ├── tokenization/   ← Paso 1: Texto → Números
│   ├── dataset/        ← Paso 2: Números → Pares
│   ├── embedding/      ← Paso 3: Números → Vectores
│   └── teoria/         ← Documentación (EMPIEZA AQUÍ)
│
└── the-verdict.txt    ← Corpus de ejemplo
```

---

## ✨ Puntos Clave Para Recordar

1. **Tokenización**: Todo texto debe ser número
2. **Vocabulario**: Cada palabra tiene un ID único
3. **Dataset**: Pares input/target entrenan predicción
4. **Embedding**: Vectores capturan significado
5. **Flujo**: Texto → Tokens → Pares → Vectores → Modelo

---

## ❓ Preguntas Comunes

**P: ¿Cuánto tiempo necesito?**
R: 30 min para lo básico, 2 horas para entender profundo.

**P: ¿Necesito matemáticas avanzadas?**
R: No. Es más conceptual que matemático.

**P: ¿Puedo correr esto sin instalar nada?**
R: Solo necesitas Java 11+ y Gradle (ya incluidos).

**P: ¿Qué es RAG?**
R: Retrieval-Augmented Generation. Explicado en `paso_a_paso_detallado.md`.

---

## 🔗 Relación Con Otros Módulos

```
01.embeddings (TÚ ESTÁS AQUÍ)
    ↓ aprende las bases
02.langchain4j
    ↓ aplica a modelos reales
03.spring-ai
    ↓ sirve en web
```

---

## 🎓 Después de Este Módulo Puedes...

- ✅ Explicar cómo funciona la tokenización
- ✅ Entender por qué los modelos usan números
- ✅ Crear un dataset para entrenamiento
- ✅ Generar embeddings y medir similitud
- ✅ Implementar búsqueda semántica (RAG)
- ✅ Depurar problemas de calidad en IA

---

## 📝 Checklist Rápido

- [ ] Leo `teoria/README.md`
- [ ] Ejecuto los 3 archivos main
- [ ] Leo `guia_interaccion_modelos_ia.md`
- [ ] Leo `paso_a_paso_detallado.md`
- [ ] Leo `por_que_cada_paso.md`
- [ ] Entiendo el flujo completo

---

**¡Listo! Abre `teoria/README.md` para empezar.**

