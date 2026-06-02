# 📚 ÍNDICE DE DOCUMENTACIÓN

## 🎯 ¿POR DÓNDE EMPEZAR?

### Para ejecutar la app inmediatamente
👉 **[SETUP.md](SETUP.md)** (5 min de lectura)
- Requisitos del sistema
- Instrucciones paso a paso
- Solución de problemas comunes

### Para entender cómo funciona
👉 **[README.md](README.md)** (20 min de lectura)
- Descripción general del proyecto
- Stack técnico
- Estructura del proyecto
- Ecuaciones de física usadas

### Para estudiosos de la arquitectura
👉 **[ARCHITECTURE.md](ARCHITECTURE.md)** (30 min de lectura)
- Diagrama de capas
- Diagrama de dependencias
- Flujo de datos completo
- Lógica de física detallada
- Patrones de diseño

### Resumen ejecutivo rápido
👉 **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** (10 min de lectura)
- Checklist de features
- Archivos implementados
- Solución de errores

---

## 📖 Documentos por Perfil

### 👨‍💻 Desarrollador (Quiero compilar y ejecutar)
1. Lee: **SETUP.md** (paso 1-5)
2. Abre Android Studio y sincroniza
3. Toca ▶ Run
4. ¡Hecho!

### 🧑‍🔬 Ingeniero (Quiero entender la arquitectura)
1. Lee: **README.md** (stack técnico + estructura)
2. Lee: **ARCHITECTURE.md** (capas + flujos)
3. Explora: `app/src/main/java/...` (código comentado)
4. Ejecuta: `./gradlew test` (valida física)

### 📚 Estudiante (Quiero aprender física + Kotlin)
1. Lee: **README.md** (ecuaciones sección 🔬)
2. Abre: `domain/Projectile.kt` (lee código + comentarios)
3. Abre: `KinematicsEngineTest.kt` (ve tests de validación)
4. Ejecuta app con diferentes parámetros
5. Compara con resultados esperados

### 🎨 Diseñador (Quiero ver la UI/UX)
1. Ejecuta la app (SETUP.md pasos 1-5)
2. Explora: Parámetros, Gráficas, Canvas
3. Modifica colores en: `app/src/main/java/ui/theme/Color.kt`

---

## 🗂️ Archivos Principales por Función

### Física (Domain Layer)
```
domain/Vector2D.kt              ← Operaciones vectoriales básicas
domain/PhysicsBody.kt           ← Interface de cuerpos
domain/Projectile.kt            ← x(t), y(t), v(t) del proyectil
domain/FallingTarget.kt         ← Objetivo que cae
domain/KinematicsEngine.kt      ← Orquestador de cálculos
domain/CollisionDetector.kt     ← Detección de choque

Conceptos clave:
- Ecuaciones parabólicas exactas
- Búsqueda de colisión refinada
- Cálculo del ángulo "tiro al mono"
```

### Aplicación (ViewModel + State)
```
data/SimulationParams.kt        ← Parámetros editables
ui/SimulationViewModel.kt       ← MVVM + StateFlow + Coroutines

Conceptos clave:
- Reactividad con StateFlow
- Animación con Coroutines
- Play/Pause/Reset control
```

### Interfaz (UI)
```
ui/SimulationScreen.kt          ← Pantalla principal
ui/components/SimulationCanvas.kt ← Canvas 2D
ui/components/PositionReadout.kt  ← Valores en vivo
ui/components/ParameterSliders.kt ← Controles
ui/components/TransportControls.kt ← Play/Pause
ui/components/TrajectoryChart.kt  ← Gráficas

Conceptos clave:
- Composables puros (sin estado)
- Material 3 Design
- Canvas dibujado proceduralmente
```

### Tests
```
KinematicsEngineTest.kt         ← 12+ pruebas de física

Validaciones:
- Posiciones en tiempo
- Velocidades constantes/variables
- Altura máxima
- Tiempo de vuelo
- Colisiones
```

---

## 🚀 Comandos Rápidos

### Compilación
```bash
./gradlew build              # Build completo
./gradlew assembleDebug      # APK debug
./gradlew clean build        # Limpia y reconstruye
```

### Ejecución
```bash
File → Run ▶ (Android Studio)
./gradlew installDebug       # Instala en emulador
```

### Tests
```bash
./gradlew test               # Ejecuta tests JUnit
Right-click KinematicsEngineTest.kt → Run (Android Studio)
```

### Debugging
```bash
./gradlew build --stacktrace # Muestra errores detallados
./gradlew clean              # Limpia caché Gradle
File → Invalidate Caches     # Reset de Android Studio
```

---

## 🎯 Casos de Uso Comunes

### "Quiero ver funcionar el tiro al mono"
```
1. Abre la app
2. Toca "⟳ Preset"
3. Toca "▶ Iniciar"
4. Observa colisión en ~1.3 segundos
```

### "Quiero cambiar la gravedad"
```
1. Tab "Parámetros"
2. Slider "Gravedad g" → Arrastra a 1.62 (Luna)
3. "▶ Iniciar"
4. Observa trayectorias más altas
```

### "Quiero validar las ecuaciones"
```
1. Terminal: ./gradlew test
2. Observa 12 tests PASSED
3. Lee KinematicsEngineTest.kt para ver qué se valida
```

### "Quiero modificar la UI"
```
1. Abre: ui/theme/Color.kt
2. Cambia valores hexadecimales de colores
3. Abre: ui/components/*.kt
4. Modifica layouts/composables
5. Toca ▶ para ver cambios
```

---

## 📊 Ecuaciones Implementadas

| Ecuación | Archivo |
|----------|---------|
| x(t) = x₀ + v₀·cos(θ)·t | Projectile.kt:L48 |
| y(t) = y₀ + v₀·sin(θ)·t − ½·g·t² | Projectile.kt:L49 |
| vₓ(t) = v₀·cos(θ) | Projectile.kt:L69 |
| vᵧ(t) = v₀·sin(θ) − g·t | Projectile.kt:L70 |
| h_max = y₀ + (v₀·sin(θ))²/(2g) | Projectile.kt:L26 |
| d = √((xₐ−xᵦ)²+(yₐ−yᵦ)²) | CollisionDetector.kt:L42 |
| θ = atan2(yᵦ−yₐ, xᵦ−xₐ) | KinematicsEngine.kt:L105 |

---

## ✅ Checklist Pre-Compilación

- [ ] Android SDK API 36 instalado (`Tools → SDK Manager`)
- [ ] JDK 17+ instalado (`java -version`)
- [ ] Android Studio Giraffe o posterior
- [ ] Espacio en disco: 2 GB mínimo
- [ ] Conexión a internet (primera compilación = build)

---

## 🆘 Help Rápido

### Pregunta: "¿Dónde están las ecuaciones?"
**Respuesta**: `domain/Projectile.kt` líneas 48-50 y comentarios

### Pregunta: "¿Cómo funciona la animación?"
**Respuesta**: `ui/SimulationViewModel.kt` método `startAnimationLoop()` línea ~85

### Pregunta: "¿Cómo se detecta la colisión?"
**Respuesta**: `domain/CollisionDetector.kt` método `detectCollision()` línea ~40

### Pregunta: "¿Qué son las pruebas?"
**Respuesta**: `KinematicsEngineTest.kt` - 12 tests que validan físi ca

### Pregunta: "¿Puedo cambiar los colores?"
**Respuesta**: `ui/theme/Color.kt` - Modifica valores hex (ej: `#4f5bd5`)

---

## 🎓 Recursos de Aprendizaje

### Conceptos en este Proyecto
- ✅ Movimiento parabólico (Física)
- ✅ MVVM (Arquitectura de software)
- ✅ StateFlow + Coroutines (Kotlin asincrónico)
- ✅ Jetpack Compose (UI declarativa)
- ✅ Material 3 Design (Diseño moderno)
- ✅ JUnit Testing (Testing unitario)
- ✅ Clean Architecture (Separación de capas)

### Libros/Artículos Relacionados
- "Now in Android" (Google) - MVVM + Compose
- "Clean Architecture" (Robert C. Martin)
- "Física: Cinemática" - Cualquier libro de Mecánica Clásica
- "Coroutines Guide" (Kotlin docs)

---

## 🎉 Siguientes Pasos

### Opción 1: Ejecutar ahora
1. Sigue SETUP.md
2. Toca ▶ Run
3. ¡Experimenta!

### Opción 2: Estudiar el código
1. Abre `domain/Projectile.kt`
2. Lee ecuaciones + comentarios
3. Compara con tests en `KinematicsEngineTest.kt`

### Opción 3: Modificar y aprender
1. Cambia gravedad en `SimulationParams.kt`
2. Modifica colores en `theme/Color.kt`
3. Recompila y observa cambios

### Opción 4: Extender funcionalidad
1. Lee ARCHITECTURE.md
2. Entiende fl ujos de datos
3. Agrega nuevas features (ej: rozamiento del aire)

---

## 📞 Contacto / Soporte

Si tienes dudas sobre:
- **Compilación**: Consulta SETUP.md
- **Física**: Consulta README.md sección "Ecuaciones"
- **Arquitectura**: Consulta ARCHITECTURE.md
- **Código**: Lee comentarios en bloques correspondientes

---

**Última actualización**: Junio 2, 2026  
**Versión del proyecto**: 1.0 ✅ Completo  
**Estado**: Listo para compilación y ejecución

¡Bienvenido! 🎯✨

