package ni.edu.uam.movimientoparabolicoapp.domain

import kotlin.math.atan2

/**
 * Motor de cinemática que centraliza todos los cálculos físicos de la aplicación.
 * 
 * Esta clase actúa como un "Orquestador", utilizando las leyes de la física para predecir
 * el comportamiento de proyectiles y objetivos bajo la influencia de la gravedad.
 *
 * @property gravity Valor de la aceleración gravitacional (m/s^2). Por defecto 9.81 (Tierra).
 */
class KinematicsEngine(
    val gravity: Double = 9.81
) {

    // Instancia del detector para manejar la lógica de proximidad entre objetos
    private val collisionDetector = CollisionDetector()

    /**
     * Crea una instancia de Proyectil con los parámetros de lanzamiento.
     * 
     * @param position Coordenada (x,y) inicial del lanzamiento.
     * @param initialSpeed Rapidez con la que sale el objeto (v0).
     * @param angleRadians Ángulo de inclinación respecto a la horizontal.
     * @return Un objeto de tipo [Projectile] configurado.
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
     * Crea una instancia del Objetivo (Objecto B), que típicamente cae libremente.
     * 
     * @param position Posición (x,y) desde donde se suelta o lanza el objetivo.
     * @param initialSpeed Rapidez inicial (0.0 para caída libre pura).
     * @param angleRadians Ángulo si se desea un lanzamiento inclinado (por defecto 0.0).
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
     * Implementación del Teorema del "Tiro al Mono".
     * 
     * Calcula el ángulo exacto para que el proyectil apunte directamente al objetivo.
     * En física, si ambos objetos caen con la misma gravedad, este ángulo garantiza
     * la colisión independientemente de la velocidad, siempre que el proyectil tenga 
     * suficiente alcance.
     *
     * @param shooterPos Posición del lanzador (A).
     * @param targetPos Posición del objetivo (B).
     * @return Ángulo en radianes calculado mediante la arcotangente (atan2).
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
     * Determina si ha ocurrido una colisión en un instante exacto de tiempo.
     */
    fun detectCollision(
        projectile: Projectile,
        target: FallingTarget,
        time: Double
    ): CollisionInfo {
        return collisionDetector.detectCollision(projectile, target, time)
    }

    /**
     * Busca colisiones dentro de un intervalo temporal (t1 a t2).
     * 
     * Es crucial para la simulación digital ya que evita que los objetos "atraviesen" 
     * entre frames si el tiempo avanza muy rápido.
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
     * Genera una lista de puntos que representan la parábola recorrida por un cuerpo.
     * 
     * @param body El cuerpo físico a analizar.
     * @param maxTime Tiempo hasta el cual se debe dibujar la línea.
     * @param samples Cantidad de puntos a generar para la curva (suavizado).
     * @return Lista de pares (tiempo, posición) para renderizado.
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
     * Estructura de datos que agrupa todos los resultados de una simulación completa.
     */
    data class SimulationStats(
        val projectileTrajectory: List<Pair<Double, Vector2D>>,
        val targetTrajectory: List<Pair<Double, Vector2D>>,
        val collisionInfo: CollisionInfo?,
        val maxTime: Double
    )

    /**
     * Realiza un análisis exhaustivo de la simulación antes de iniciar la animación.
     * 
     * Calcula los tiempos de vuelo de ambos cuerpos y genera sus trayectorias completas
     * para que la interfaz pueda dibujar las líneas de predicción (punteadas).
     */
    fun calculateSimulationStats(
        projectile: Projectile,
        target: FallingTarget,
        maxSimulationTime: Double = 10.0
    ): SimulationStats {
        val projectileFlightTime = projectile.getFlightTime()
        val targetFlightTime = target.getFlightTime()
        
        // El tiempo de análisis es el máximo que dure cualquiera de los dos cuerpos en el aire
        val maxTime = minOf(
            maxOf(projectileFlightTime, targetFlightTime),
            maxSimulationTime
        )

        val projTraj = generateTrajectory(projectile, maxTime)
        val targetTraj = generateTrajectory(target, maxTime)

        // Busca si ocurrirá un choque en algún momento de la trayectoria
        val collision = findCollisionInInterval(projectile, target, 0.0, maxTime)

        return SimulationStats(
            projectileTrajectory = projTraj,
            targetTrajectory = targetTraj,
            collisionInfo = collision,
            maxTime = maxTime
        )
    }

    /**
     * Verifica la distancia euclidiana entre dos objetos en un tiempo 't'.
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
