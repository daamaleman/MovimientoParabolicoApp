package ni.edu.uam.movimientoparabolicoapp.domain

import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Motor de cinemática que maneja todas las operaciones de cálculo de movimiento parabólico.
 *
 * Proporciona métodos para:
 * - Crear y actualizar cuerpos
 * - Calcular posiciones y velocidades
 * - Detectar colisiones
 * - Calcular magnitudes derivadas (altura máxima, alcance, tiempo de vuelo, etc.)
 */
class KinematicsEngine(
    val gravity: Double = 9.81  // Aceleración gravitacional en m/s^2
) {

    private val collisionDetector = CollisionDetector()

    /**
     * Crea un proyectil con los parámetros dados.
     */
    fun createProjectile(
        position: Vector2D,
        initialSpeed: Double,
        angleRadians: Double
    ): Projectile {
        return Projectile(
            initialPosition = position,
            initialSpeed = initialSpeed,
            angleRadians = angleRadians,
            gravity = gravity
        )
    }

    /**
     * Crea un objetivo que cae.
     */
    fun createFallingTarget(
        position: Vector2D,
        initialSpeed: Double = 0.0,
        angleRadians: Double = 0.0
    ): FallingTarget {
        return FallingTarget(
            initialPosition = position,
            initialSpeed = initialSpeed,
            angleRadians = angleRadians,
            gravity = gravity
        )
    }

    /**
     * Calcula el ángulo de apuntamiento necesario para hacer que dos cuerpos colisionen.
     *
     * Heurística del "tiro al mono": si ambos cuerpos experimentan la misma gravedad,
     * entonces apuntar al vector que conecta las posiciones iniciales garantiza
     * que sus trayectorias relativas mantengan la línea de visión.
     *
     * @param shooterPos Posición inicial del tirador (cuerpo A)
     * @param targetPos Posición inicial del objetivo (cuerpo B)
     * @return Ángulo en radianes (0 a π/2)
     */
    fun calculateMonkeyHunterAngle(
        shooterPos: Vector2D,
        targetPos: Vector2D
    ): Double {
        val dx = targetPos.x - shooterPos.x
        val dy = targetPos.y - shooterPos.y
        return atan2(dy, dx)
    }

    /**
     * Detecta colisión en un tiempo específico.
     */
    fun detectCollision(
        projectile: Projectile,
        target: FallingTarget,
        time: Double
    ): CollisionInfo {
        return collisionDetector.detectCollision(projectile, target, time)
    }

    /**
     * Busca la colisión más precisa en un intervalo de tiempo.
     */
    fun findCollisionInInterval(
        projectile: Projectile,
        target: FallingTarget,
        startTime: Double,
        endTime: Double
    ): CollisionInfo? {
        return collisionDetector.findCollisionInInterval(projectile, target, startTime, endTime)
    }

    /**
     * Obtiene la distancia mínima entre dos cuerpos en un intervalo.
     * Retorna un Pair(tiempo de distancia mínima, distancia mínima)
     */
    fun getMinimumDistance(
        projectile: Projectile,
        target: FallingTarget,
        startTime: Double,
        endTime: Double
    ): Pair<Double, Double> {
        return collisionDetector.getMinimumDistance(projectile, target, startTime, endTime)
    }

    /**
     * Genera los puntos de la trayectoria de un cuerpo para su visualización.
     *
     * @param body Cuerpo del cual calcular la trayectoria
     * @param maxTime Tiempo máximo (ej: tiempo de vuelo del cuerpo)
     * @param samples Número de muestras para la trayectoria
     * @return Lista de pares (tiempo, posición) ordenados por tiempo
     */
    fun generateTrajectory(
        body: PhysicsBody,
        maxTime: Double,
        samples: Int = 500
    ): List<Pair<Double, Vector2D>> {
        val trajectory = mutableListOf<Pair<Double, Vector2D>>()

        if (maxTime <= 0) return trajectory

        for (i in 0..samples) {
            val t = maxTime * (i / samples.toDouble())
            val position = body.getPosition(t)
            trajectory.add(Pair(t, position))
        }

        return trajectory
    }

    /**
     * Calcula estadísticas totales del sistema hasta un tiempo dado o frontera de colisión.
     */
    data class SimulationStats(
        val projectileTrajectory: List<Pair<Double, Vector2D>>,
        val targetTrajectory: List<Pair<Double, Vector2D>>,
        val collisionInfo: CollisionInfo?,
        val maxTime: Double
    )

    /**
     * Calcula estadísticas completas de la simulación.
     */
    fun calculateSimulationStats(
        projectile: Projectile,
        target: FallingTarget,
        maxSimulationTime: Double = 10.0  // segundos máximos para simular
    ): SimulationStats {
        // Calcula hasta el mínimo entre el tiempo de vuelo y el tiempo máximo de simulación
        val projectileFlightTime = projectile.getFlightTime()
        val targetFlightTime = target.getFlightTime()
        val maxTime = minOf(
            maxOf(projectileFlightTime, targetFlightTime),
            maxSimulationTime
        )

        // Genera trayectorias
        val projTraj = generateTrajectory(projectile, maxTime)
        val targetTraj = generateTrajectory(target, maxTime)

        // Busca colisión
        val collision = findCollisionInInterval(projectile, target, 0.0, maxTime)

        return SimulationStats(
            projectileTrajectory = projTraj,
            targetTrajectory = targetTraj,
            collisionInfo = collision,
            maxTime = maxTime
        )
    }

    /**
     * Verifica si dos cuerpos aún están "en el aire" (y > 0).
     */
    fun isInAir(body: PhysicsBody, time: Double): Boolean {
        return body.getPosition(time).y > 0
    }

    /**
     * Obtiene la distancia actual entre dos cuerpos.
     */
    fun getDistance(
        bodyA: PhysicsBody,
        bodyB: PhysicsBody,
        time: Double
    ): Double {
        val posA = bodyA.getPosition(time)
        val posB = bodyB.getPosition(time)
        return posA.distanceTo(posB)
    }
}

