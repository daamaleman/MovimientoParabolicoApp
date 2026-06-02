# ARQUITECTURA Y DISEÑO - Resumen Completo

## 🏗️ Visión General del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                    APLICACIÓN: Tiro Parabólico                  │
├─────────────────────────────────────────────────────────────────┤
│                         UI LAYER (Compose)                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │Sim. Screen  │  │Components    │  │Theme (M3)    │           │
│  └──────────────┘  └──────────────┘  └──────────────┘           │
├─────────────────────────────────────────────────────────────────┤
│                      VIEWMODEL (MVVM)                           │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │ SimulationViewModel (StateFlow + Coroutines)              │ │
│  │ ├─ simulationState: StateFlow<SimulationState>            │ │
│  │ ├─ play() / pause() / reset()                             │ │
│  │ └─ updateParams() / calculateMonkeyHunter Angle()         │ │
│  └────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│                       DATA LAYER                                │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │ SimulationParams (Data Class)                             │ │
│  │ ├─ projectileInitialSpeed, Angle, Pos(x,y)               │ │
│  │ ├─ targetPos(x,y), initialSpeed                          │ │
│  │ ├─ gravity, animationSpeedMultiplier                      │ │
│  │ └─ Presets: moonPreset(), marsPreset(), ...              │ │
│  └────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│                    DOMAIN LAYER (Física Pura)                   │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │ PhysicsBody (Interface)                                   │ │
│  │ ├─ getPosition(t) → Vector2D                              │ │
│  │ ├─ getVelocity(t) → Vector2D                              │ │
│  │ └─ getFlightTime() → Double                               │ │
│  ├────┬────────────────────────────────────────────────────┤ │
│  │    │ Projectile (extends PhysicsBody)                    │ │
│  │    ├─ initialPos, initialSpeed, angle, gravity            │ │
│  │    ├─ maxHeight, horizontalRange (properties)             │ │
│  │    └─ getPosition/Velocity/Speed (implementations)        │ │
│  │                                                            │ │
│  │    │ FallingTarget (extends PhysicsBody)                 │ │
│  │    ├─ Modo: caída libre (v₀=0) o lanzado (v₀>0)         │ │
│  │    └─ getPosition/Velocity/Speed (implementations)        │ │
│  ├────┬────────────────────────────────────────────────────┤ │
│  │    │ KinematicsEngine (Orquestador)                     │ │
│  │    ├─ createProjectile() → Projectile                    │ │
│  │    ├─ createFallingTarget() → FallingTarget              │ │
│  │    ├─ detectCollision() → CollisionInfo                  │ │
│  │    ├─ findCollisionInInterval() → CollisionInfo?         │ │
│  │    ├─ getMinimumDistance() → (time, distance)            │ │
│  │    ├─ generateTrajectory() → List<(t, pos)>              │ │
│  │    ├─ calculateMonkeyHunterAngle() → Double              │ │
│  │    └─ calculateSimulationStats() → SimulationStats       │ │
│  │                                                            │ │
│  │    │ CollisionDetector (Especializado)                   │ │
│  │    ├─ threshold: Double = 0.001 m                         │ │
│  │    ├─ detectCollision() → CollisionInfo                  │ │
│  │    ├─ findCollisionInInterval() → CollisionInfo?         │ │
│  │    └─ getMinimumDistance() → (time, distance)            │ │
│  └────┴────────────────────────────────────────────────────┘ │
│                                                                │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │ Vector2D (Data Class + Operadores)                        │ │
│  │ ├─ x: Double, y: Double                                   │ │
│  │ ├─ magnitude → Double (sqrt(x² + y²))                    │ │
│  │ ├─ + (suma), - (resta), * (escalar), / (escalar)          │ │
│  │ └─ distanceTo(other) → Double                             │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📦 Dependencias de Módulos

```
SimulationScreen.kt
├─ depends on → SimulationViewModel
├─ depends on → SimulationState (del ViewModel)
├─ embeds → SimulationCanvas
├─ embeds → PositionReadout
├─ embeds → ParameterSliders
├─ embeds → TransportControls
├─ embeds → CollisionBanner
└─ embeds → TrajectoryChart

SimulationViewModel.kt
├─ depends on → KinematicsEngine
├─ depends on → Projectile
├─ depends on → FallingTarget
├─ depends on → SimulationParams
├─ uses → StateFlow + Coroutines
└─ emits → SimulationState

KinematicsEngine.kt
├─ depends on → Projectile
├─ depends on → FallingTarget
├─ depends on → CollisionDetector
├─ uses → Vector2D (vectores)
└─ solves → Ecuaciones parabólicas

Projectile.kt & FallingTarget.kt
├─ implements → PhysicsBody
├─ uses → Vector2D
└─ implements → Ecuaciones de movimiento

CollisionDetector.kt
├─ uses → PhysicsBody (interfaz genérica)
├─ uses → Vector2D
└─ returns → CollisionInfo

Vector2D.kt
└─ Data class puro (sin dependencias)
```

---

## 🔄 Flujo de Datos y Control

### Inicialización
```
MainActivity onCreate()
  ↓
  setContent { SimulationScreen() }
    ↓
    viewModel: SimulationViewModel = viewModel()
      ↓
      init { updateParams(SimulationParams.monkeyHunterPreset()) }
        ↓
        KinematicsEngine.createProjectile() + createFallingTarget()
          ↓
          KinematicsEngine.calculateSimulationStats()
            ↓
            _simulationState.value = SimulationState(...)
              ↓
              UI re-compone con estado inicial

Estado inicial listo → Listo para Play
```

### Loop de Animación (Play → ▶)
```
viewModel.play()
  ↓
  startAnimationLoop() en viewModelScope.launch
    ├─ while (isRunning) Loop
    │   ├─ Calcula deltaTime del frame anterior
    │   ├─ Crea cuerpos Projectile + FallingTarget con params actuales
    │   ├─ KinematicsEngine.getPosition(newTime) para ambos
    │   ├─ KinematicsEngine.detectCollision() 
    │   │   ├─ Si ocurre → shouldStop = true, guarda CollisionInfo
    │   │   └─ Si no → continúa
    │   ├─ Actualiza _simulationState con nuevas posiciones
    │   ├─ UI re-compone automáticamente (StateFlow reactivo)
    │   ├─ delay(16) → ~60 FPS
    │   └─ Continúa loop si !shouldStop
    │
    └─ Final: Launch finaliza, animación se detiene

Canvas + Readouts se actualizan en cada frame
```

### Parámetro Cambió (Usuario mueve un slider)
```
ParameterSliders.onParamChange(newParams)
  ↓
  SimulationScreen → ViewModel.updateParams(newParams)
    ↓
    Stop current animation loop (si estaba corriendo)
    ↓
    Recalcula engine.calculateSimulationStats()
    ↓
    Regenera nuevas trayectorias
    ↓
    _simulationState = SimulationState con nuevos valores
    ↓
    simStatus.currentTime = 0.0, isRunning = false
    ↓
    UI se redibuja con nuevos parámetros (Canvas + Readouts)

Usuario puede presionar Play nuevamente
```

---

## 🧮 Lógica de Física (Domain Layer)

### Clase Projectile

**Ecuación Base**:
```kotlin
class Projectile(
    initialPosition: Vector2D,    // (x₀, y₀)
    initialSpeed: Double,          // v₀ (m/s)
    angleRadians: Double,          // θ (rad)
    gravity: Double                // g (m/s²)
)
```

**Propiedades Calculadas**:
```kotlin
v0x = v0 * cos(θ)
v0y = v0 * sin(θ)

maxHeight = y₀ + (v0y)² / (2g)

getFlightTime(): Double {
    // Resuelve: y(t) = y₀ + v0y*t - 0.5*g*t² = 0
    // Retorna: t = (v0y + sqrt(v0y² + 2*g*y₀)) / g
}
```

**Métodos**:
```kotlin
getPosition(t: Double): Vector2D {
    return Vector2D(
        x = x₀ + v0x * t,
        y = y₀ + v0y * t - 0.5 * g * t²
    )
}

getVelocity(t: Double): Vector2D {
    return Vector2D(
        vx = v0x,           // constante
        vy = v0y - g * t
    )
}

getSpeed(t: Double): Double {
    val v = getVelocity(t)
    return sqrt(v.x² + v.y²)
}
```

### Clase FallingTarget

**Similar a Projectile pero con dos modos**:
- Modo A: v₀ = 0 → Caída libre vertical
  ```
  x(t) = x₀ (constante)
  y(t) = y₀ - 0.5 * g * t²
  ```

- Modo B: v₀ > 0 → Se lanza hacia el proyectil
  ```
  x(t) = x₀ + v0x * t
  y(t) = y₀ + v0y * t - 0.5 * g * t²
  ```

### Clase CollisionDetector

```kotlin
fun detectCollision(
    bodyA: PhysicsBody,
    bodyB: PhysicsBody,
    time: Double
): CollisionInfo {
    val posA = bodyA.getPosition(time)
    val posB = bodyB.getPosition(time)
    val distance = posA.distanceTo(posB)
    
    return if (distance < threshold) {  // 0.001 m
        CollisionInfo(
            occurred = true,
            time = time,
            position = (posA + posB) / 2.0,
            distance = distance
        )
    } else {
        CollisionInfo(occurred = false, ...)
    }
}
```

### Efecto "Tiro al Mono"

**Principio Físico**:
```
Si dos objetos están bajo influencia de la misma g,
entonces la dirección relativa entre ellos NO cambia.

Por lo tanto, si apuntas en la dirección de su posición
inicial relativa, SIEMPRE chocarán (si las distancias
y tiempos de vuelo lo permiten).

θ_óptimo = atan2(yᵦ - yₐ, xᵦ - xₐ)
```

**Implementación**:
```kotlin
fun calculateMonkeyHunterAngle(
    shooterPos: Vector2D,
    targetPos: Vector2D
): Double {
    return atan2(
        targetPos.y - shooterPos.y,  // Δy
        targetPos.x - shooterPos.x   // Δx
    )
}
```

---

## 🎨 UI Layer - Composables

### SimulationScreen (Contenedor Principal)

```
Scaffold(
  topBar = { TopAppBar(...) }
  ├─ SimulationCanvas        → 280.dp height
  ├─ CollisionBanner         → Si collision.occurred
  ├─ PositionReadout         → x, y, v en vivo
  ├─ TransportControls       → Play/Pause/Reset
  ├─ TabRow                  → Parámetros | Gráficas
  └─ when (tab) {
       0 → ParameterSliders  → Sliders editables
       1 → TrajectoryChart   → Gráficas y(x)
     }
)
```

### Componentes Principales

#### ParameterSliders
```
Dos secciones con Sliders:

┌─ Objeto A — Proyectil
│  ├─ Velocidad v₀ (1–40 m/s)
│  ├─ Ángulo θ (0–90°)
│  └─ Posición (x₀, y₀)
│
├─ Objeto B — Objetivo
│  ├─ Posición (x, y)
│  ├─ Velocidad v₀ (0–20 m/s)
│  └─ Helper: "Si = 0, cae libremente"
│
└─ Entorno
   ├─ Gravedad g (1.6–24.8 m/s²)
   └─ Velocidad de animación (0.2×–3×)
```

#### SimulationCanvas
```
Canvas dibuja:
├─ Fondo + Suelo (línea verde)
├─ Cuadrícula de referencia
├─ Trayectorias punteadas (predicción completa)
├─ Trayectorias sólidas (hasta tiempo actual)
├─ Objetos A (azul) y B (naranja)
├─ Punto de colisión (verde destello) si ocurrió
└─ Time badge: "t = 0.XX s"
```

#### PositionReadout
```
Grid de 2 tarjetas:
┌─────────────────┬─────────────────┐
│ Objeto A        │ Objeto B        │
│ x: 5.23 m       │ x: 10.14 m      │
│ y: 8.91 m       │ y: 0.45 m       │
│ v: 12.3 m/s     │ v: 8.7 m/s      │
└─────────────────┴─────────────────┘

+ Row: Tiempo | Distancia
```

#### TransportControls
```
┌────────────────────────────┬──────┐
│ ▶ Iniciar / ❚❚ Detener    │ ⟲    │
│ (Play/Pause botón grande)  │Reset │
└────────────────────────────┴──────┘
```

---

## 🧪 Testing (JUnit)

### KinematicsEngineTest.kt

```kotlin
@Test fun testInitialPosition()           → Verifica x(0)=x₀, y(0)=y₀
@Test fun testPositionAtT1()              → Valida ecuaciones en t=1
@Test fun testConstantHorizontalVelocity()→ Vx(t) = const
@Test fun testLinearVerticalVelocity()    → Vy decae con -g*t
@Test fun testMaxHeight()                 → h_max correcta
@Test fun testSpeed()                     → |v(t)| = sqrt(vx²+vy²)
@Test fun testCollisionDetection()        → d < threshold
@Test fun testMonkeyHunterAngle()         → atan2 correcto
@Test fun testFlightTime()                → Raíz positiva de y(t)=0
@Test fun testVector2DDistance()          → Distancia euclidiana
@Test fun testVector2DOperations()        → +, -, *, /
@Test fun testFreefall()                  → y decrece con t
```

---

## 🔐 Principios de Diseño Aplicados

### 1. Separación de Concerns
- **Domain**: Física pura, independiente de Android
- **Data**: Modelos de datos editables
- **UI**: Presentación y control de usuario
- **ViewModel**: Orquestación entre capas

### 2. MVVM Pattern
- **Model**: SimulationParams + domain classes
- **View**: Composables (SimulationScreen + components)
- **ViewModel**: SimulationViewModel (StateFlow + Coroutines)

### 3. Reactividad con StateFlow
```kotlin
private val _simulationState = MutableStateFlow(...)
val simulationState = _simulationState.asStateFlow()

// UI observa cambios y re-compone automáticamente
val state by viewModel.simulationState.collectAsState()
```

### 4. Programación Orientada a Objetos
- **PhysicsBody**: Interface, permite polimorfismo
- **Projectile & FallingTarget**: Implementaciones concretas
- **Data classes**: Vector2D, CollisionInfo, SimulationParams
- **Sealed classes**: Posible para tipos de cuerpo (futura extensión)

### 5. Functional Programming
- Lambdas: `onParamChange: (SimulationParams) -> Unit`
- Higher-order functions: `generatesTrajectory(maxTime, samples)`
- Operaciones inmutables: `data.copy(field = newValue)`

### 6. Coroutines para Animación
```kotlin
viewModelScope.launch {
    // No bloquea el hilo principal
    // Puede ser cancelada automáticamente en onCleared()
    while (isRunning) {
        // Actualiza estado
        delay(16)  // ~60 FPS
    }
}
```

---

## 📊 Flujo de Estados

```
INICIAL
  ↓
[State: t=0, isRunning=false, collisionInfo=null]
  ↓
Usuario toca ▶ Play
  ↓
[State: isRunning=true] → startAnimationLoop() inicia
  ↓
REPRODUCCIÓN
  ├─ Cada frame (16ms):
  │  ├─ t += deltaTime * speedMultiplier
  │  ├─ Calcula posiciones/velocidades nuevas
  │  ├─ Detecta colisión
  │  └─ Emite nuevo State → UI re-compone
  │
  └─ Si ocurre colisión O ambos tocan suelo:
     └─ [State: isRunning=false, collisionInfo=CollisionInfo(...)]
        ↓
        PAUSADO EN COLISIÓN (o fin de vuelo)
          ↓
          Usuario toca ▶ de nuevo
            ↓
            reset() → t=0, collisionInfo=null
              ↓
              Vuelve a INICIAL
```

---

## 🎯 Casos de Uso Cubiertos

### Use Case 1: Simular Tiro al Mono
```
1. User toca "Preset"
2. Parámetros → preset_monkey_hunter()
3. Canvas dibuja escena
4. User toca ▶ Play
5. Loop anima hasta colisión (~1.3s)
6. CollisionBanner aparece
7. User toca Reset para repetir
```

### Use Case 2: Verificar Ecuaciones
```
1. User configura parámetros manualmente
2. Parámetros → calculateSimulationStats()
3. Se generan trayectorias y(x)
4. TrajectoryChart visualiza
5. Readouts muestran valores en tiempo real
6. Tests corroboran exactitud matemática
```

### Use Case 3: Cambiar Gravedad
```
1. Slider de gravedad → 1.62 (Luna)
2. updateParams() regenera trayectorias
3. Canvas redibuja con trayectorias más "altas"
4. Tiempo de colisión aumenta
5. User compara con Tierra/Marte
```

---

## 📈 Performance & Optimización

### Animación
- **Target**: 60 FPS (16 ms por frame)
- **Medida**: `delay(16)` en coroutine loop
- **Optimización**: NO regenerar trayectorias en cada frame (solo en paramChange)

### Memoria
- **StateFlow + Coroutines**: Canceladas automáticamente en onCleared()
- **Canvas**: Re-drawea solo lo visible (Compose optimiza)
- **Vectores**: Data classes (lightweight)

### CPU
- **Ecuaciones**: Cálculos analíticos O(1), NO O(n)
- **Loop**: Coroutine no bloquea UI thread
- **Renders**: Compose batcha invalidaciones

---

Este documento sirve como referencia de arquitectura completa.
Para cambios o extensiones futuras, consulta este diagrama.

*Arquitectura diseñada: Junio 2, 2026*

