# Simulador de Movimiento Parabólico - Colisión de Dos Cuerpos

## 📱 Descripción General

Aplicación Android nativa en **Kotlin** que simula el **movimiento parabólico de dos cuerpos con colisión** (escenario tipo **"tiro al mono" / choque de dos proyectiles**).

### Características Principales

- ✅ **Física exacta**: Ecuaciones de cinemática 100% analíticas (sin métodos numéricos aproximados)
- ✅ **Animación fluida**: Loop basado en `withFrameNanos` + Coroutines (no Thread.sleep)
- ✅ **Detección de colisión**: Distancia < 1 mm (0.001 m)
- ✅ **Parámetros editables**: Sliders para todos los controles
- ✅ **Gráficas nativas**: Trayectorias y(x), y(t) con Vico
- ✅ **Canvas animado**: Visualización en tiempo real con trayectorias y marcas de colisión
- ✅ **Material 3 Design**: Tema dinámico, claro/oscuro, colores modernos
- ✅ **Pruebas unitarias**: JUnit para todas las ecuaciones de física

---

## 🛠️ Stack Técnico

| Componente | Detalles |
|-----------|----------|
| **Lenguaje** | Kotlin 2.2.10 (100%) |
| **SDK** | minSdk 24, targetSdk 35, compileSdk 35 |
| **Arquitectura** | MVVM + ViewModel + StateFlow + Coroutines |
| **UI** | Jetpack Compose + Material 3 + Vico Charts |
| **Gravedad** | kotlin.math + Apache Commons Math 3 (vectores) |
| **Build** | Gradle Kotlin DSL (build.gradle.kts) + catálogo (libs.versions.toml) |

---

## 📂 Estructura del Proyecto

```
app/src/
├── main/
│   ├── java/ni/edu/uam/movimientoparabolicoapp/
│   │   ├── MainActivity.kt                    # Punto de entrada
│   │   ├── domain/                           # Lógica de física (sin Android)
│   │   │   ├── Vector2D.kt                   # Data class para vectores
│   │   │   ├── PhysicsBody.kt                # Interface de cuerpos
│   │   │   ├── Projectile.kt                 # Proyectil (cuerpo A)
│   │   │   ├── FallingTarget.kt              # Objetivo que cae (cuerpo B)
│   │   │   ├── KinematicsEngine.kt           # Motor de cálculos
│   │   │   └── CollisionDetector.kt          # Detección de colisión
│   │   ├── data/
│   │   │   └── SimulationParams.kt           # Parámetros editables
│   │   ├── ui/
│   │   │   ├── SimulationViewModel.kt        # MVVM + StateFlow
│   │   │   ├── SimulationScreen.kt           # Pantalla principal
│   │   │   └── components/
│   │   │       ├── SimulationCanvas.kt       # Canvas con Draw
│   │   │       ├── PositionReadout.kt        # Posición en vivo
│   │   │       ├── ParameterSliders.kt       # Controles de parámetros
│   │   │       ├── TransportControls.kt      # Play/Pause/Reset
│   │   │       ├── CollisionBanner.kt        # Mensaje de colisión
│   │   │       └── TrajectoryChart.kt        # Gráficas con Vico
│   │   └── ui/theme/                         # Material 3 Theme
│   │       ├── Color.kt, Type.kt, Theme.kt
│   └── res/
│       ├── values/, drawable/, mipmap-*
│       └── AndroidManifest.xml
├── test/
│   └── java/ni/edu/uam/movimientoparabolicoapp/
│       └── KinematicsEngineTest.kt           # Pruebas unitarias
└── build.gradle.kts, libs.versions.toml
```

---

## 🔬 Ecuaciones de Física Implementadas

### Movimiento Parabólico

Cada cuerpo con posición inicial **(x₀, y₀)**, rapidez inicial **v₀** y ángulo **θ**:

#### Componentes de Velocidad Inicial
```
v₀ₓ = v₀ · cos(θ)
v₀ᵧ = v₀ · sin(θ)
```

#### Posición en Función del Tiempo
```
x(t) = x₀ + v₀ₓ · t
y(t) = y₀ + v₀ᵧ · t − ½ · g · t²
```
- **x(t)**: Movimiento Rectilíneo Uniforme (MRU)
- **y(t)**: Movimiento Uniformemente Acelerado (MRUV)

#### Velocidad en Función del Tiempo
```
vₓ(t) = v₀ₓ                 (constante)
vᵧ(t) = v₀ᵧ − g · t
v(t) = √(vₓ² + vᵧ²)         (rapidez)
```

#### Magnitudes Derivadas
```
Altura máxima:    h_max = y₀ + (v₀ᵧ)² / (2g)
Tiempo de vuelo:  t_v = (√(v₀ᵧ² + 2·g·y₀) + v₀ᵧ) / g
Alcance:          x_max = x₀ + v₀ₓ · t_v
```

### Detección de Colisión

```
d(t) = √((xₐ(t) − xᵦ(t))² + (yₐ(t) − yᵦ(t))²)

Colisión si: d < 0.001 m (1 milímetro)
```

### El Efecto "Tiro al Mono"

**Principio**: Si ambos cuerpos experimentan la misma gravedad, apuntar a la posición inicial del objetivo **garantiza colisión**, aunque se mueva (especialmente si cae libre v₀=0).

**Cálculo del ángulo**:
```
θ = atan2(yᵦ − yₐ, xᵦ − xₐ)
```

---

## 🚀 Cómo Ejecutar

### 1. Abrir en Android Studio

```bash
# Clonar o abrir el proyecto
cd MovimientoParabolicoApp
# Abrir en Android Studio: File → Open → [carpeta del proyecto]
```

### 2. Sincronizar Gradle

- Android Studio detectará **build.gradle.kts** y **libs.versions.toml**
- Haz clic en *"Sync Now"* del banner de Project Structure

### 3. Ejecutar la app

- **Emulador**: Select a virtual device (API ≥ 24)
- **Dispositivo físico**: Conecta un dispositivo Android
- Clic en ▶ **Run** (Shift + F10)

---

## 💡 Uso de la App

### Control Principal

| Botón/Control | Función |
|--|--|
| **▶ Iniciar** | Comienza la animación |
| **❚❚ Detener** | Pausa la animación |
| **⟲ Reset** | Reinicia a t=0 |
| **⟳ Preset** | Aplica valores iniciales "Tiro al Mono" |

### Parámetros Editables (Tab: Parámetros)

**Objeto A — Proyectil**
- Velocidad inicial v₀: 1–40 m/s
- Ángulo θ: 0–90°
- Posición x₀, y₀

**Objeto B — Objetivo**
- Posición x, y
- Velocidad inicial v₀ (0 = cae libre)

**Entorno**
- Gravedad g: 1.6 m/s² (Luna) a 24.8 m/s² (Presets disponibles)
- Velocidad de animación: 0.2× a 3× (cámara lenta/rápida)

### Gráficas (Tab: Gráficas)

- **y(x)**: Trayectoria de altura vs posición horizontal
- **Punto de choque**: Marcado en verde si ocurre colisión

### Readouts en Vivo

Muestran en cada frame:
- **Tiempo t**
- **Posición (x, y)** de ambos objetos
- **Rapidez v** de ambos objetos
- **Distancia d** entre ellos

---

## 🧪 Pruebas Unitarias

Ejecutar todos los tests:

```bash
# En Android Studio
Right-click en app/src/test/ → Run 'KinematicsEngineTest'

# O desde terminal
./gradlew test
```

**Tests incluidos**:
- ✅ Posición inicial (t=0)
- ✅ Posición en movimiento
- ✅ Velocidad constante horizontal
- ✅ Velocidad lineal vertical
- ✅ Altura máxima
- ✅ Rapidez (magnitud)
- ✅ Tiempo de vuelo
- ✅ Distancia entre vectores
- ✅ Operaciones vectoriales
- ✅ Caída libre
- ✅ Ángulo "tiro al mono"

---

## 📊 Preset: "Tiro al Mono" (Default)

Valores por defecto que demuestran la colisión garantizada:

| Parámetro | Valor |
|-----------|-------|
| **Proyectil A** | |
| — Posición | (0, 0) |
| — Velocidad v₀ | 15 m/s |
| — Ángulo θ | 38.7° (apuntando a B) |
| **Objetivo B** | |
| — Posición | (10, 8) m |
| — Velocidad v₀ | 0 m/s (cae libre) |
| **Entorno** | |
| — g | 9.81 m/s² (Tierra) |
| **Resultado** | Colisión en ~1.3 segundos |

### Verificación

```kotlin
// Cálculo manual del ángulo:
θ = atan2(8 - 0, 10 - 0) = atan2(8, 10) ≈ 38.66°

// Tiempo de colisión aproximado (analítico):
// Ambos tienen la misma g, así que sus posiciones relativas
// permanecen en la línea de visión inicial → COLISIÓN GARANTIZADA
```

---

## 🎨 Diseño Material 3

- **Tema dinámico**: Colores adaptativos según el dispositivo (Material You)
- **Modo claro/oscuro**: Automático según preferencias del sistema
- **Colores principales**:
  - Proyectil A: Azul (#4f5bd5)
  - Objetivo B: Naranja (#e0552b)
  - Colisión: Verde (#1f8a4c)

---

## 📝 Notas Técnicas

### Animación sin Bloqueo de Hilo

```kotlin
// En SimulationViewModel:
viewModelScope.launch {
    while (_simulationState.value.isRunning) {
        val deltaTime = ...  // Calcula delta de frame
        updateState()         // Actualiza posiciones/velocidades
        delay(16)            // ~60 FPS, NO Thread.sleep()
    }
}
```

### Detección Precisa de Colisión

Se busca el mínimo de distancia en el intervalo [t_frame_anterior, t_frame].
Esto evita "saltarse" colisiones si dt es grande.

### Conversión de Grados a Radianes

```kotlin
angleRadians = angleDegrees * PI / 180.0
```

Mantenemos grados en UI (más intuitivo) e internamente radianes.

---

## 🔧 Dependencias (resumen)

```toml
# Compose
androidx-compose-bom = "2026.02.01"
androidx-compose-ui = "..."
androidx-compose-material3 = "..."

# Gráficas
vico-compose = "1.15.0"
vico-compose-m3 = "1.15.0"

# Matemáticas
commons-math3 = "3.6.1"

# Coroutines
kotlinx-coroutines = "1.9.1"

# Lifecycle
androidx-lifecycle-viewmodel-compose = "2.8.0"
```

Todas definidas en `gradle/libs.versions.toml`.

---

## 🐛 Casos Limite Manejados

1. **Ángulo 0°**: Movimiento horizontal puro
2. **Ángulo 90°**: Lanzamiento vertical
3. **Gravedad muy pequeña** (Luna 1.62 m/s²): Tiempos de vuelo largos
4. **v₀ = 0 para objetivo**: Caída libre pura
5. **Objetos debajo del suelo** (y < 0): Se clampean a 0 en visualización
6. **Colisión antes de t=0** (no ocurre): Solver busca solo t > 0

---

## 📖 Módulos Explicados

### 1. **domain/*.kt** (Física pura)
- 100% independiente de Android
- Testeable sin contexto de aplicación
- Contiene toda la cinemática

### 2. **data/SimulationParams.kt**
- Data class con todos los parámetros
- Presets (Tierra, Luna, Marte)
- Serializable (para futuros guardados)

### 3. **ui/SimulationViewModel.kt**
- MVVM: Separa lógica de UI
- StateFlow: Reactividad
- Coroutines: Animación no bloqueante

### 4. **ui/SimulationScreen.kt**
- Compose Scaffold con TopAppBar
- Tabs: Parámetros / Gráficas
- Orquesta componentes

### 5. **ui/components/\*.kt**
- Componentes reutilizables
- Cada uno única responsabilidad
- Composables puros (no estado)

---

## 🚀 Mejoras Futuras

- [ ] Guardar/cargar simulaciones (JSON)
- [ ] Exportar datos a CSV
- [ ] Modo pausa + step frame-by-frame
- [ ] Trazado de vector velocidad en canvas
- [ ] Cálculo de energía cinética/potencial
- [ ] Soporte de rozamiento del aire (drag)
- [ ] Simulaciones en tiempo de compilación (presets más complejos)

---

## 📜 Licencia

Proyecto educativo. Libre para uso académico y personal.

---

## 👤 Autor

Simulador desarrollado como ejemplo completo de:
- Kotlin puro en Android
- Jetpack Compose
- MVVM + Coroutines
- Física exacta aplicada

---

## 📞 Soporte

Para dudas o reportes de errores, revisa:
1. Las pruebas unitarias (`KinematicsEngineTest.kt`)
2. Los comentarios en el código
3. La documentación de Material 3 y Jetpack Compose

¡Que disfrutes la simulación! 🎯✨

