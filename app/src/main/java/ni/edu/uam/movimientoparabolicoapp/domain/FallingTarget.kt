package ni.edu.uam.movimientoparabolicoapp.domain

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Representa el Objeto B (Objetivo) de la simulación.
 *
 * El objetivo puede comportarse de dos formas:
 * 1. Caída Libre: Si su rapidez inicial es 0, simplemente cae verticalmente.
 * 2. Lanzamiento: Si tiene rapidez inicial, describe una parábola similar al proyectil.
 */
class FallingTarget(
    val initialPosition: Vector2D,
    val initialSpeed: Double,       
    val angleRadians: Double = 0.0, 
    val gravity: Double = 9.81      
) : PhysicsBody {

    // Componentes de velocidad inicial. 
    // Si initialSpeed es 0, ambos componentes serán 0 (caída libre pura).
    private val v0x: Double = initialSpeed * cos(angleRadians)
    private val v0y: Double = initialSpeed * sin(angleRadians)

    /**
     * Calcula la posición (x, y) en el instante 't'.
     * 
     * Sigue el mismo principio de superposición que el Proyectil, pero usualmente 
     * inicia desde una altura mayor.
     */
    override fun getPosition(t: Double): Vector2D {
        val x = initialPosition.x + v0x * t
        val y = initialPosition.y + v0y * t - 0.5 * gravity * t * t
        return Vector2D(x, y)
    }

    /**
     * Calcula la velocidad instantánea (vx, vy).
     */
    override fun getVelocity(t: Double): Vector2D {
        val vx = v0x
        val vy = v0y - gravity * t
        return Vector2D(vx, vy)
    }

    /**
     * Determina cuánto tiempo tarda el objetivo en tocar el suelo.
     */
    override fun getFlightTime(): Double {
        if (initialPosition.y <= 0) return 0.0

        val discriminant = v0y * v0y + 2 * gravity * initialPosition.y
        if (discriminant < 0) return 0.0

        val t1 = (v0y + sqrt(discriminant)) / gravity
        val t2 = (v0y - sqrt(discriminant)) / gravity

        return if (t1 > 0 && t2 > 0) maxOf(t1, t2) else maxOf(t1, t2, 0.0)
    }
}
