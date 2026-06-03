package ni.edu.uam.movimientoparabolicoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ni.edu.uam.movimientoparabolicoapp.data.SimulationParams
import ni.edu.uam.movimientoparabolicoapp.domain.CollisionInfo
import ni.edu.uam.movimientoparabolicoapp.domain.KinematicsEngine
import ni.edu.uam.movimientoparabolicoapp.domain.Vector2D

/**
 * Estado de la simulación en cada frame/actualización.
 */
data class SimulationState(
    val params: SimulationParams = SimulationParams(),
    val currentTime: Double = 0.0,
    val isRunning: Boolean = false,
    val collisionInfo: CollisionInfo? = null,
    val projectilePos: Vector2D = Vector2D(),
    val projectileVelocity: Vector2D = Vector2D(),
    val projectileSpeed: Double = 0.0,
    val targetPos: Vector2D = Vector2D(),
    val targetVelocity: Vector2D = Vector2D(),
    val targetSpeed: Double = 0.0,
    val currentDistance: Double = Double.MAX_VALUE,
    val maxSimulationTime: Double = 10.0,
    val projectileTrajectory: List<Pair<Double, Vector2D>> = emptyList(),
    val targetTrajectory: List<Pair<Double, Vector2D>> = emptyList()
)

/**
 * ViewModel que gestiona la simulación usando MVVM.
 */
class SimulationViewModel : ViewModel() {

    private val _simulationState = MutableStateFlow(SimulationState())
    val simulationState = _simulationState.asStateFlow()

    private var animationJob: Job? = null

    init {
        updateParams(SimulationParams.monkeyHunterPreset())
    }

    /**
     * Actualiza los parámetros de la simulación y recalcula las trayectorias.
     */
    fun updateParams(newParams: SimulationParams) {
        val kinematicsEngine = KinematicsEngine(gravity = newParams.gravity)

        val projectile = kinematicsEngine.createProjectile(
            position = Vector2D(newParams.projectileX, newParams.projectileY),
            initialSpeed = newParams.projectileInitialSpeed,
            angleRadians = newParams.getProjectileAngleRadians()
        )

        val target = kinematicsEngine.createFallingTarget(
            position = Vector2D(newParams.targetX, newParams.targetY),
            initialSpeed = newParams.targetInitialSpeed
        )

        val stats = kinematicsEngine.calculateSimulationStats(
            projectile = projectile,
            target = target,
            maxSimulationTime = 15.0
        )

        _simulationState.update { currentState ->
            currentState.copy(
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
    }

    fun updateParam(updater: (SimulationParams) -> SimulationParams) {
        updateParams(updater(_simulationState.value.params))
    }

    fun play() {
        if (_simulationState.value.isRunning) return

        // Auto-reset si ya terminó
        val state = _simulationState.value
        if (state.collisionInfo != null || state.currentTime >= state.maxSimulationTime) {
            reset()
        }

        _simulationState.update { it.copy(isRunning = true) }
        startAnimationLoop()
    }

    fun pause() {
        _simulationState.update { it.copy(isRunning = false) }
        animationJob?.cancel()
    }

    fun reset() {
        animationJob?.cancel()
        updateParams(_simulationState.value.params)
    }

    private fun startAnimationLoop() {
        animationJob?.cancel()
        animationJob = viewModelScope.launch {
            var lastFrameTime = System.nanoTime()

            while (_simulationState.value.isRunning) {
                val currentFrameTime = System.nanoTime()
                val deltaTimeSecs = (currentFrameTime - lastFrameTime) / 1_000_000_000.0
                lastFrameTime = currentFrameTime

                val state = _simulationState.value
                val params = state.params
                
                val adjustedDeltaTime = deltaTimeSecs * params.animationSpeedMultiplier
                val newTime = state.currentTime + adjustedDeltaTime

                val kinematicsEngine = KinematicsEngine(gravity = params.gravity)
                val projectile = kinematicsEngine.createProjectile(
                    position = Vector2D(params.projectileX, params.projectileY),
                    initialSpeed = params.projectileInitialSpeed,
                    angleRadians = params.getProjectileAngleRadians()
                )

                val target = kinematicsEngine.createFallingTarget(
                    position = Vector2D(params.targetX, params.targetY),
                    initialSpeed = params.targetInitialSpeed
                )

                val projectilePos = projectile.getPosition(newTime)
                val targetPos = target.getPosition(newTime)
                
                val projectileInAir = projectilePos.y >= -0.01 
                val targetInAir = targetPos.y >= -0.01

                // Usamos búsqueda en el intervalo para no saltarnos el choque entre frames
                val collision = kinematicsEngine.findCollisionInInterval(
                    projectile, target, state.currentTime, newTime
                ) ?: kinematicsEngine.detectCollision(projectile, target, newTime)
                
                val shouldStop = collision.occurred || (!projectileInAir && !targetInAir) || newTime > state.maxSimulationTime

                _simulationState.update {
                    it.copy(
                        currentTime = if (collision.occurred) collision.time else newTime,
                        isRunning = !shouldStop,
                        collisionInfo = if (collision.occurred) collision else null,
                        projectilePos = if (collision.occurred) projectile.getPosition(collision.time) else (if (projectileInAir) projectilePos else projectilePos.copy(y = 0.0)),
                        targetPos = if (collision.occurred) target.getPosition(collision.time) else (if (targetInAir) targetPos else targetPos.copy(y = 0.0)),
                        projectileVelocity = projectile.getVelocity(newTime),
                        projectileSpeed = projectile.getSpeed(newTime),
                        targetVelocity = target.getVelocity(newTime),
                        targetSpeed = target.getSpeed(newTime),
                        currentDistance = if (collision.occurred) collision.distance else kinematicsEngine.getDistance(projectile, target, newTime)
                    )
                }

                if (shouldStop) break
                delay(16)
            }
        }
    }

    fun calculateMonkeyHunterAngle(): Double {
        val params = _simulationState.value.params
        val kinematicsEngine = KinematicsEngine(gravity = params.gravity)
        val shooterPos = Vector2D(params.projectileX, params.projectileY)
        val targetPos = Vector2D(params.targetX, params.targetY)

        return Math.toDegrees(kinematicsEngine.calculateMonkeyHunterAngle(shooterPos, targetPos))
    }

    fun applyMonkeyHunterPreset() {
        updateParams(SimulationParams.monkeyHunterPreset())
    }

    fun applyMoonPreset() {
        updateParams(SimulationParams.moonPreset())
    }

    fun applyMarsPreset() {
        updateParams(SimulationParams.marsPreset())
    }
}
