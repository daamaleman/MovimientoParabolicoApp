package ni.edu.uam.movimientoparabolicoapp.domain

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Representa el objetivo (cuerpo B) que cae desde una posición inicial.
 *
 * Puede estar en dos modos:
 * 1. Si v0 = 0: cae en caída libre vertical desde (x0, y0)
 *    x(t) = x0 (constante)
 *    y(t) = y0 - 0.5 * g * t^2
 *
 * 2. Si v0 > 0: se lanza hacia el proyectil con un ángulo calculado
 *    (típicamente hacia abajo y hacia la izquierda si el proyectil está en x=0)
 *    x(t) = x0 + v0x * t
 *    y(t) = y0 + v0y * t - 0.5 * g * t^2
 */
class FallingTarget(
    val initialPosition: Vector2D,
    val initialSpeed: Double,       // v0 en m/s (0 = caída libre vertical)
    val angleRadians: Double = 0.0, // ángulo en radianes (usado si v0 > 0)
    val gravity: Double = 9.81      // g en m/s^2
) : PhysicsBody {

    private val v0x: Double = initialSpeed * cos(angleRadians)
    private val v0y: Double = initialSpeed * sin(angleRadians)

    /**
     * Altura máxima (relevante solo si se lanza hacia arriba, v0y > 0)
     */
    val maxHeight: Double
        get() {
            return if (v0y > 0) {
                initialPosition.y + (v0y * v0y) / (2 * gravity)
            } else {
                initialPosition.y
            }
        }

    /**
     * Calcula la posición en el tiempo t
     */
    override fun getPosition(t: Double): Vector2D {
        val x = initialPosition.x + v0x * t
        val y = initialPosition.y + v0y * t - 0.5 * gravity * t * t
        return Vector2D(x, y)
    }

    /**
     * Calcula la velocidad en el tiempo t
     */
    override fun getVelocity(t: Double): Vector2D {
        val vx = v0x
        val vy = v0y - gravity * t
        return Vector2D(vx, vy)
    }

    /**
     * Tiempo de vuelo: cuando y(t) = 0
     */
    override fun getFlightTime(): Double {
        if (initialPosition.y <= 0) return 0.0

        val discriminant = v0y * v0y + 2 * gravity * initialPosition.y
        if (discriminant < 0) return 0.0

        val t1 = (v0y + sqrt(discriminant)) / gravity
        val t2 = (v0y - sqrt(discriminant)) / gravity

        return if (t1 > 0 && t2 > 0) maxOf(t1, t2) else maxOf(t1, t2, 0.0)
    }

    override fun toString(): String {
        val modeStr = if (initialSpeed == 0.0) "caída libre" else "lanzado"
        return "FallingTarget(pos=$initialPosition, v0=$initialSpeed m/s, mode=$modeStr)"
    }
}

