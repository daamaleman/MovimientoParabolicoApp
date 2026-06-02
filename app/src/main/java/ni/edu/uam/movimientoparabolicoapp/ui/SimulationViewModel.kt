package ni.edu.uam.movimientoparabolicoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ni.edu.uam.movimientoparabolicoapp.data.SimulationParams
import ni.edu.uam.movimientoparabolicoapp.domain.CollisionInfo
import ni.edu.uam.movimientoparabolicoapp.domain.KinematicsEngine
import ni.edu.uam.movimientoparabolicoapp.domain.Vector2D

/**
 * Estado de la simulación en cada frame/actualización.
 */
data class SimulationState(
    // Parámetros actuales
    val params: SimulationParams = SimulationParams(),

    // Tiempo transcurrido en la simulación
    val currentTime: Double = 0.0,

    // ¿Está en reproducción?
    val isRunning: Boolean = false,

    // ¿Ocurrió una colisión?
    val collisionInfo: CollisionInfo? = null,

    // Posición actual del proyectil A
    val projectilePos: Vector2D = Vector2D(),
    val projectileVelocity: Vector2D = Vector2D(),
    val projectileSpeed: Double = 0.0,

    // Posición actual del objetivo B
    val targetPos: Vector2D = Vector2D(),
    val targetVelocity: Vector2D = Vector2D(),
    val targetSpeed: Double = 0.0,

    // Distancia actual entre cuerpos
    val currentDistance: Double = Double.MAX_VALUE,

    // Tiempo máximo de simulación calculado
    val maxSimulationTime: Double = 10.0,

    // Trayectorias calculadas para gráficas/visualización
    val projectileTrajectory: List<Pair<Double, Vector2D>> = emptyList(),
    val targetTrajectory: List<Pair<Double, Vector2D>> = emptyList()
)

/**
 * ViewModel que gestiona la simulación usando MVVM.
 *
 * Responsabilidades:
 * - Mantener el estado de la simulación
 * - Ejecutar el loop de animación con withFrameNanos/LaunchedEffect
 * - Manejar play/pause/reset
 * - Actualizar los parámetros de entrada
 * - Detección de colisiones
 */
class SimulationViewModel : ViewModel() {

    private val kinematicsEngine = KinematicsEngine()

    private val _simulationState = MutableStateFlow(SimulationState())
    val simulationState = _simulationState.asStateFlow()

    init {
        // Inicializa con parámetros por defecto del "tiro al mono"
        updateParams(SimulationParams.monkeyHunterPreset())
    }

    /**
     * Actualiza los parámetros de la simulación y recalcula las trayectorias.
     */
    fun updateParams(newParams: SimulationParams) {
        val projectile = kinematicsEngine.createProjectile(
            position = Vector2D(newParams.projectileX, newParams.projectileY),
            initialSpeed = newParams.projectileInitialSpeed,
            angleRadians = newParams.getProjectileAngleRadians()
        )

        val target = kinematicsEngine.createFallingTarget(
            position = Vector2D(newParams.targetX, newParams.targetY),
            initialSpeed = newParams.targetInitialSpeed,
            angleRadians = 0.0  // El objetivo cae verticalmente (v0=0) o hacia el proyectil
        )

        // Calcula estadísticas completas
        val stats = kinematicsEngine.calculateSimulationStats(
            projectile = projectile,
            target = target,
            maxSimulationTime = 10.0
        )

        // Reinicia la simulación con los nuevos parámetros
        _simulationState.value = SimulationState(
            params = newParams,
            currentTime = 0.0,
            isRunning = false,
            collisionInfo = null,
            projectilePos = projectile.getPosition(0.0),
            projectileVelocity = projectile.getVelocity(0.0),
            projectileSpeed = projectile.getSpeed(0.0),
            targetPos = target.getPosition(0.0),
            targetVelocity = target.getVelocity(0.0),
            targetSpeed = target.getSpeed(0.0),
            currentDistance = kinematicsEngine.getDistance(projectile, target, 0.0),
            maxSimulationTime = stats.maxTime,
            projectileTrajectory = stats.projectileTrajectory,
            targetTrajectory = stats.targetTrajectory
        )
    }

    /**
     * Actualiza solo un parámetro específico (ej: velocidad inicial).
     * Mantiene los demás sin cambios.
     */
    fun updateParam(updater: (SimulationParams) -> SimulationParams) {
        updateParams(updater(_simulationState.value.params))
    }

    /**
     * Inicia la reproducción de la simulación.
     */
    fun play() {
        _simulationState.value = _simulationState.value.copy(isRunning = true)
        startAnimationLoop()
    }

    /**
     * Pausa la reproducción.
     */
    fun pause() {
        _simulationState.value = _simulationState.value.copy(isRunning = false)
    }

    /**
     * Reinicia la simulación a t=0 sin cambiar parámetros.
     */
    fun reset() {
        _simulationState.value = _simulationState.value.copy(
            currentTime = 0.0,
            isRunning = false,
            collisionInfo = null
        )
        updateParams(_simulationState.value.params)
    }

    /**
     * Loop de animación que se ejecuta en cada frame usando coroutines.
     * Se ejecuta en el scope del ViewModel para ser cancelado automáticamente.
     */
    private fun startAnimationLoop() {
        viewModelScope.launch {
            var lastFrameTime = System.nanoTime()

            while (_simulationState.value.isRunning) {
                val currentFrameTime = System.nanoTime()
                val deltaTimeNanos = currentFrameTime - lastFrameTime
                val deltaTimeSecs = deltaTimeNanos / 1_000_000_000.0

                // Aplica el multiplicador de velocidad de animación
                val adjustedDeltaTime = deltaTimeSecs * _simulationState.value.params.animationSpeedMultiplier

                // Actualiza el tiempo
                val newTime = _simulationState.value.currentTime + adjustedDeltaTime

                // Crea cuerpos con parámetros actuales
                val projectile = kinematicsEngine.createProjectile(
                    position = Vector2D(
                        _simulationState.value.params.projectileX,
                        _simulationState.value.params.projectileY
                    ),
                    initialSpeed = _simulationState.value.params.projectileInitialSpeed,
                    angleRadians = _simulationState.value.params.getProjectileAngleRadians()
                )

                val target = kinematicsEngine.createFallingTarget(
                    position = Vector2D(
                        _simulationState.value.params.targetX,
                        _simulationState.value.params.targetY
                    ),
                    initialSpeed = _simulationState.value.params.targetInitialSpeed
                )

                // Obtiene posiciones y velocidades actuales
                val projectilePos = projectile.getPosition(newTime)
                val projectileVel = projectile.getVelocity(newTime)
                val targetPos = target.getPosition(newTime)
                val targetVel = target.getVelocity(newTime)

                // Verifica si ambos están en el aire
                val projectileInAir = kinematicsEngine.isInAir(projectile, newTime)
                val targetInAir = kinematicsEngine.isInAir(target, newTime)

                // Detecta colisión
                val collision = kinematicsEngine.detectCollision(projectile, target, newTime)

                // Detiene si hay colisión o ambos tocan el suelo
                val shouldStop = collision.occurred || (!projectileInAir && !targetInAir)

                val distance = kinematicsEngine.getDistance(projectile, target, newTime)

                // Actualiza el estado
                _simulationState.value = _simulationState.value.copy(
                    currentTime = newTime,
                    isRunning = !shouldStop,
                    collisionInfo = if (collision.occurred) collision else null,
                    projectilePos = if (projectileInAir) projectilePos else projectilePos.copy(y = 0.0),
                    projectileVelocity = projectileVel,
                    projectileSpeed = projectileVel.magnitude,
                    targetPos = if (targetInAir) targetPos else targetPos.copy(y = 0.0),
                    targetVelocity = targetVel,
                    targetSpeed = targetVel.magnitude,
                    currentDistance = distance
                )

                if (shouldStop) {
                    break
                }

                lastFrameTime = currentFrameTime

                // Pequeña pausa para no bloquear el hilo (evita busy-waiting)
                kotlinx.coroutines.delay(16)  // ~60 FPS
            }
        }
    }

    /**
     * Calcula el ángulo de apuntamiento para hacer "tiro al mono".
     */
    fun calculateMonkeyHunterAngle(): Double {
        val shooterPos = Vector2D(
            _simulationState.value.params.projectileX,
            _simulationState.value.params.projectileY
        )
        val targetPos = Vector2D(
            _simulationState.value.params.targetX,
            _simulationState.value.params.targetY
        )

        val angleRadians = kinematicsEngine.calculateMonkeyHunterAngle(shooterPos, targetPos)
        return Math.toDegrees(angleRadians)
    }

    /**
     * Aplica el preset de "tiro al mono".
     */
    fun applyMonkeyHunterPreset() {
        val preset = SimulationParams.monkeyHunterPreset()
        updateParams(preset)
    }

    /**
     * Aplica preset de la Luna.
     */
    fun applyMoonPreset() {
        updateParams(SimulationParams.moonPreset())
    }

    /**
     * Aplica preset de Marte.
     */
    fun applyMarsPreset() {
        updateParams(SimulationParams.marsPreset())
    }
}

