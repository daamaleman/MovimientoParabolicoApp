# INSTRUCCIONES DE COMPILACIÓN Y EJECUCIÓN

## 📋 Resumen Rápido

Este es un proyecto Android completo en **Kotlin** que simula colisión de proyectiles parabólicos (efecto "tiro al mono").

### Características Principales
- ✅ Motor de física 100% analítico (ecuaciones exactas)
- ✅ Animación fluida con Coroutines
- ✅ Detección de colisión < 1 mm
- ✅ UI moderna con Jetpack Compose + Material 3
- ✅ Parámetros editables con sliders
- ✅ Gráficas de trayectorias
- ✅ Pruebas unitarias JUnit

---

## 🚀 Cómo Compilar y Ejecutar

### 1. **Requisitos Previos**
- Android Studio **Giraffe** o posterior
- JDK 17 o superior
- Android SDK API 36 instalado

### 2. **Abrir el Proyecto**
```bash
File → Open → [Carpeta del proyecto: MovimientoParabolicoApp]
```

### 3. **Sincronizar Gradle**
- Android Studio detectará `build.gradle.kts` y `libs.versions.toml`
- Haz clic en **"Sync Now"** cuando veas el banner

### 4. **Seleccionar Dispositivo**
- **Opción A (Emulador)**: 
  - Tools → Device Manager → Crear/Seleccionar un virtual device (API ≥ 24)
  
- **Opción B (Dispositivo físico)**: 
  - Conecta un teléfono Android con USB
  - Habilita "Developer Mode" en el teléfono

### 5. **Ejecutar la Aplicación**
```bash
Haz clic en Run ▶ (o Shift + F10)
```

---

## ✅ Si hay Errores de Compilación

### Error: "Could not find compiledSdk"
**Solución**: Actualiza Android SDK
```
Tools → SDK Manager → SDK Platforms → Instala API Level 36
```

### Error: "Unresolved reference to coroutines"
**Solución**: Invalida caché de Gradle
```
File → Invalidate Caches → Invalidate and Restart
```

### Error: Imports no resueltos
**Solución**: Haz clic derecho en el archivo → Run Quick Fixes

---

## 🧪 Ejecutar Pruebas Unitarias

```bash
# Opción 1: Desde Android Studio (Recommended)
Right-click en: app/src/test/java/ni/edu/uam/movimientoparabolicoapp/KinematicsEngineTest.kt
→ Run

# Opción 2: Desde terminal
cd MovimientoParabolicoApp
./gradlew test
```

**Pruebas incluidas:**
- ✅ Posición inicial y en movimiento
- ✅ Velocidad horizontal constante y vertical lineal
- ✅ Altura máxima y alcance
- ✅ Rapidez (magnitud del vector velocidad)
- ✅ Tiempo de vuelo
- ✅ Distancia entre vectores
- ✅ Caída libre
- ✅ Ángulo de apuntamiento ("tiro al mono")

---

## 📱 Uso Básico de la App

### Valores Iniciales (Preset)
Por defecto carga el escenario **"Tiro al Mono"**:
- **Proyectil A**: (0, 0), v₀ = 15 m/s, θ ≈ 38.7°
- **Objetivo B**: (10, 8) m, v₀ = 0 (cae libre)
- **Gravedad**: 9.81 m/s² (Tierra)
- **Resultado**: Colisión garantizada en ~1.3 s

### Botones Principales
| Botón | Función |
|-------|---------|
| **▶ Iniciar** | Comienza la animación |
| **❚❚ Detener** | Pausa la animación |
| **⟲ Reset** | Reinicia a t = 0 |
| **⟳ Preset** | Recarga valores iniciales |

### Parámetros Editables (Tab: Parámetros)
- Velocidad inicial del proyectil (1–40 m/s)
- Ángulo de lanzamiento (0–90°)
- Posición inicial de ambos objetos
- Velocidad del objetivo (0 = caída libre)
- Gravedad (presets: Tierra, Luna, Marte)
- Velocidad de animación (cámara lenta/rápida)

### Gráficas (Tab: Gráficas)
- **y(x)**: Altura vs Posición horizontal
- Punto de colisión marcado en verde

---

## 🏗️ Estructura de Archivos Implementada

```
✅ DOMINIO (Física Pura - Sin Android)
   ├── Vector2D.kt                   Data class con operadores
   ├── PhysicsBody.kt                Interface base
   ├── Projectile.kt                 Movimiento parabólico
   ├── FallingTarget.kt              Caída libre / lanzado
   ├── KinematicsEngine.kt           Motor de cálculos
   └── CollisionDetector.kt          Detección de choque

✅ DATOS
   └── SimulationParams.kt           Parámetros editables + presets

✅ UI (Jetpack Compose + Material 3)
   ├── SimulationViewModel.kt        MVVM + StateFlow + Coroutines
   ├── SimulationScreen.kt           Pantalla principal
   ├── components/
   │   ├── SimulationCanvas.kt       Canvas 2D con trayectorias
   │   ├── PositionReadout.kt        Posición en vivo
   │   ├── ParameterSliders.kt       Controles de parámetros
   │   ├── TransportControls.kt      Play/Pause/Reset
   │   ├── CollisionBanner.kt        Mensaje de colisión
   │   └── TrajectoryChart.kt        Gráficas y(x)
   └── theme/                        Material 3 colors/typography

✅ TESTS (JUnit)
   └── KinematicsEngineTest.kt       12+ casos de prueba

✅ CONFIGURACIÓN
   ├── build.gradle.kts              Dependencias + compilación
   ├── libs.versions.toml            Catálogo de versiones
   ├── AndroidManifest.xml           Configuración de app
   └── README.md + SETUP.md          Documentación
```

---

## 💾 Dependencias Instaladas

```kotlin
// Compose BOM (Jetpack Compose)
androidx.compose:compose-bom:2026.02.01

// Material 3
androidx.compose.material3:material3

// Compose UI
androidx.compose.ui:ui
androidx.compose.ui:ui-graphics
androidx.compose.ui:ui-tooling-preview

// Lifecycle + ViewModel
androidx.lifecycle:lifecycle-runtime-ktx:2.10.0
androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0
androidx.lifecycle:lifecycle-runtime-compose:2.8.0

// Coroutines
kotlinx.coroutines:kotlinx-coroutines-core:1.7.3
kotlinx.coroutines:kotlinx-coroutines-android:1.7.3

// Apache Commons Math (para operaciones vectoriales)
commons-math3:3.6.1

// Testing
junit:junit:4.13.2
androidx.test.ext:junit:1.3.0
androidx.test.espresso:espresso-core:3.7.0
```

---

## 📊 Ecuaciones de Física Implementadas

### Movimiento Parabólico Básico
```
x(t) = x₀ + v₀·cos(θ)·t
y(t) = y₀ + v₀·sin(θ)·t − ½·g·t²

vₓ(t) = v₀·cos(θ)           [constante]
vᵧ(t) = v₀·sin(θ) − g·t
```

### Magnitudes Derivadas
```
Altura máxima:    hₘₐₓ = y₀ + (v₀·sin(θ))² / (2g)
Tiempo de vuelo:  tᵥ = (v₀·sin(θ) + √(v₀²·sin²(θ) + 2g·y₀)) / g
Alcance:          xₘₐₓ = x₀ + v₀·cos(θ)·tᵥ
Rapidez:          |v(t)| = √(vₓ² + vᵧ²)
```

### Detección de Colisión
```
d(t) = √((xₐ(t) − xᵦ(t))² + (yₐ(t) − yᵦ(t))²)

COLISIÓN si: d < 0.001 m  (1 milímetro)
```

### Efecto "Tiro al Mono"
```
θ = atan2(yᵦ − yₐ, xᵦ − xₐ)

✓ Principio: Ambos cuerpos con la misma g → línea de visión se mantiene
✓ Resultado: COLISIÓN GARANTIZADA aunque B se mueva (especialmente si cae)
```

---

## 🎯 Casos de Uso Listos

### 1. **Tiro al Mono Clásico** ✅
- Proyectil: (0,0), 15 m/s, 38.7°
- Objetivo: (10,8), cae libre (v₀=0)
- Resultado: Colisión ~1.3 s

### 2. **Proyectil vs Proyectil**
- Ambos lanzados
- Configurar velocidades y ángulos
- Variar gravedad

### 3. **Efecto Gravedad**
- Comparar Tierra (9.81) vs Luna (1.62) vs Marte (3.71)
- Observar tiempos de vuelo más largos
- Trarayectorias más "planas"

### 4. **Cámara Lenta / Rápida**
- Slider de "Velocidad de Animación": 0.2× a 3×
- Analizar en detalle las trayectorias
- Verificar instante exacto de colisión

---

## 🔬 Verificación de Física

### Test Manual Recomendado
```
1. Preset → Cargar "Tiro al Mono"
2. Play → Ver colisión en ~1.3 s
3. Reset → Pausar en t = 0.65 s
   → Verificar que ambos están aproximadamente 
     en el mismo punto YA
4. Cambiar gravedad a Luna (1.62)
   → Observar trayectorias más altas
   → Colisión más tardía
5. Canvas → Ver trayectorias graficadas
```

---

## 📝 Archivos Generados

✅ **Java/Kotlin** (9 archivos):
- `domain/`: 6 archivos (física pura)
- `data/`: 1 archivo (parámetros)
- `ui/`: 8 archivos (UI + components)
- `MainActivity.kt`: Punto de entrada

✅ **Configuración** (3 archivos):
- `build.gradle.kts`: Dependencias + build
- `libs.versions.toml`: Catálogo de versiones
- `AndroidManifest.xml`: Configuración de app

✅ **Tests** (1 archivo):
- `KinematicsEngineTest.kt`: 12+ pruebas

✅ **Documentación** (2 archivos):
- `README.md`: Guía completa
- `SETUP.md`: Este archivo

---

## 🐛 Si Algo no Funciona

### Problema: Parámetros no se actualizan
**Causa**: StateFlow no reactivo
**Solución**: Asegúrate que `updateParams()` es llamado

### Problema: Colisión no se detecta
**Causa**: Threshold de distancia (< 0.001 m) muy pequeño
**Verificar**: `CollisionDetector.kt` línea ~ 35

### Problema: Animación muy rápida/lenta
**Causa**: `animationSpeedMultiplier` fuera de rango
**Solución**: Usa slider (0.2× a 3×)

### Problema: Canvas no dibuja
**Causa**: Trayectorias vacías
**Solución**: Verifica que `generateTrajectory()` retorna datos

---

## 📞 Soporte Rápido

| Problema | Solución |
|----------|----------|
| Gradle sync falla | `File → Invalidate Caches → Restart` |
| compileSdk error | Instala API 36 en SDK Manager |
| Imports rojos | Right-click → `Run Quick Fixes` |
| Tests no corren | `./gradlew test` desde terminal |
| App se congela | Reduce `animationSpeedMultiplier` |

---

## 🎓 Concepto Educativo

Esta aplicación demuestra:
1. **Física Analítica**: Ecuaciones exactas, no aproximaciones numéricas
2. **Arquitetura Limpia**: Domain layer puro, separación de concerns
3. **MVVM + Reactive**: StateFlow, ViewModel, Coroutines
4. **UI Moderna**: Jetpack Compose + Material 3
5. **Testing**: Pruebas unitarias del motor
6. **Kotlin Avanzado**: Data classes, sealed classes, extension functions

---

## ✨ ¡Listo para Compilar!

```bash
# Terminal (desde el proyecto)
./gradlew build

# Android Studio
Run ▶ (Shift + F10)
```

**Resultado esperado**: Aplicación compilada y ejecutando en ~30–60 segundos.

¡Que disfrutes la simulación! 🎯✨

---
*Última actualización: Junio 2, 2026*

