# 🎯 MovimientoParabolicoApp

**Simulador Cinemático de Alto Desempeño para Android**

`MovimientoParabolicoApp` es una plataforma educativa avanzada desarrollada en **Kotlin** y **Jetpack Compose** que permite modelar, visualizar y analizar el movimiento parabólico de proyectiles y la colisión de dos cuerpos en tiempo real (escenario clásico del "Tiro al Mono").

---

## 🏗️ Arquitectura del Sistema

El proyecto sigue una arquitectura **MVVM (Model-View-ViewModel)** robusta, desacoplando completamente la lógica física de la representación visual.

### Capas del Proyecto:

1.  **Domain (Capa de Física):**
    *   **Independencia Total:** Escrito en Kotlin puro, sin dependencias de Android. Esto permite que el motor de física sea portable y fácilmente testeable.
    *   **Motor Cinemático (`KinematicsEngine`):** Encargado de instanciar cuerpos físicos (`Projectile`, `FallingTarget`) y orquestar el cálculo de trayectorias.
    *   **Detector de Colisiones (`CollisionDetector`):** Implementa algoritmos de proximidad euclidiana con un umbral de precisión de **1 milímetro (0.001m)**.

2.  **UI State & ViewModel:**
    *   **Unidirectional Data Flow (UDF):** El estado de la simulación se gestiona mediante un `StateFlow` reactivo en `SimulationViewModel`.
    *   **Loop de Animación Optimizado:** Utiliza Coroutines (`viewModelScope.launch`) con un control preciso de `deltaTime` (basado en `System.nanoTime()`), garantizando una fluidez de 60 FPS sin bloquear el hilo principal de la interfaz.

3.  **UI Components (Jetpack Compose):**
    *   **Composición Declarativa:** Interfaz construida íntegramente con Compose, utilizando Material Design 3.
    *   **Custom Drawing:** Uso intensivo de `androidx.compose.foundation.Canvas` para la representación de trayectorias y el entorno físico.

---

## 🔬 Especificaciones Técnicas de Física

### 1. Ecuaciones Cinemáticas
El simulador utiliza modelos analíticos exactos para determinar la posición $(x, y)$ y velocidad $(v_x, v_y)$ en cualquier instante $t$:

*   **Posición Horizontal (MRU):** $x(t) = x_0 + (v_0 \cdot \cos\theta) \cdot t$
*   **Posición Vertical (MRUV):** $y(t) = y_0 + (v_0 \cdot \sin\theta) \cdot t - \frac{1}{2}gt^2$
*   **Velocidad Instantánea:** $v(t) = \sqrt{v_x^2 + (v_y - gt)^2}$

### 2. Detección de Colisiones por Intervalos
Para evitar el efecto de "túnel" (donde los objetos se cruzan entre frames sin detectarse), el motor implementa una **búsqueda de colisión por intervalos**:
*   En cada frame, se analiza el segmento de tiempo $[t_{anterior}, t_{actual}]$.
*   Se utiliza `findCollisionInInterval` para determinar si el punto de contacto exacto ocurrió en medio del salto temporal.

### 3. Teorema del "Tiro al Mono"
La aplicación incluye lógica para calcular automáticamente el ángulo de interceptación:
$$\theta = \arctan\left(\frac{y_{objetivo} - y_{lanzador}}{x_{objetivo} - x_{lanzador}}\right)$$
Si ambos cuerpos caen bajo la misma gravedad $g$, la colisión está garantizada independientemente de la velocidad inicial $v_0$.

---

## 🎨 Diseño y UX (User Experience)

### Modo Oscuro Permanente
La aplicación está diseñada bajo un esquema de **Modo Oscuro Permanente**, optimizado para el análisis de gráficas y visualización de alto contraste:
*   **ProjectileBlue (`#8B95F6`)** y **TargetOrange (`#FF8B66`)**: Tonos neón vibrantes que aseguran visibilidad sobre el fondo profundo.
*   **Surface Design:** Uso de `surfaceContainer` y elevaciones tonales para separar los controles de la visualización principal.

### Visualización Dinámica
*   **Canvas de Simulación:** Renderizado 2D con suelo anclado dinámicamente al origen físico $(0,0)$.
*   **Gráfica Analítica y(x):**
    *   Implementa efectos de **Glow** (brillo) en las líneas de trayectoria.
    *   **Sombreado de área** bajo la curva para mejorar la percepción de altura.
    *   **Seguimiento dinámico:** Marcadores móviles que indican la posición actual de los objetos en la gráfica analítica.
*   **Notificaciones Suaves:** El sistema de alerta de colisión utiliza un diseño de tarjeta minimalista con transparencias, mostrando el tiempo e impacto en coordenadas exactas.

---

## 🛠️ Stack Tecnológico

| Tecnología | Propósito |
| :--- | :--- |
| **Kotlin 2.0+** | Lenguaje de programación principal. |
| **Jetpack Compose** | Toolkit moderno para UI declarativa. |
| **Material 3** | Sistema de diseño de última generación. |
| **Kotlin Coroutines** | Gestión de concurrencia y loop de simulación. |
| **StateFlow** | Gestión reactiva del estado de la aplicación. |
| **JUnit 4** | Suite de pruebas unitarias para el motor físico. |

---

## 📂 Estructura del Código Fuente

```
ni.edu.uam.movimientoparabolicoapp/
├── data/
│   └── SimulationParams.kt         # Estructura de datos de configuración y presets.
├── domain/
│   ├── Vector2D.kt                # Operaciones vectoriales personalizadas.
│   ├── PhysicsBody.kt             # Abstracción de cuerpos con masa y trayectoria.
│   ├── KinematicsEngine.kt        # Orquestador de cálculos cinemáticos.
│   └── CollisionDetector.kt       # Algoritmos de detección de impacto.
├── ui/
│   ├── SimulationViewModel.kt     # Controlador del estado y loop de animación.
│   ├── SimulationScreen.kt        # Orquestador de la UI principal (Scaffold).
│   ├── components/                # Librería de componentes visuales (Canvas, Sliders, Charts).
│   └── theme/                     # Definición de colores, tipografía y estilo oscuro.
└── MainActivity.kt                # Punto de entrada de la aplicación.
```

---

## 🚀 Instalación y Desarrollo

1.  **Requisitos:** Android Studio Ladybug (o superior) y JDK 17.
2.  **Clonación:** `git clone https://github.com/tu-usuario/MovimientoParabolicoApp.git`
3.  **Build:** Sincronizar Gradle y ejecutar en un dispositivo con API 24 o superior.

---

## 🧪 Validación y Calidad
El proyecto incluye una amplia suite de **Pruebas Unitarias** que validan:
*   Conservación del movimiento horizontal.
*   Precisión del tiempo de vuelo ($t_{v}$).
*   Cálculo exacto del punto de colisión bajo diferentes gravedades ($g_{tierra}, g_{luna}, g_{marte}$).

---

**Desarrollado con ❤️ para la enseñanza de la Física y la Ingeniería de Software.**
