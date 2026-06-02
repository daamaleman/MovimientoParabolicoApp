package ni.edu.uam.movimientoparabolicoapp.domain

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Representa un proyectil (cuerpo A) lanzado con velocidad inicial v0 y ángulo theta.
 *
 * Ecuaciones de movimiento parabólico:
 *   x(t) = x0 + v0x * t
 *   y(t) = y0 + v0y * t - 0.5 * g * t^2
 *
 * donde:
 *   v0x = v0 * cos(theta)
 *   v0y = v0 * sin(theta)
 */
class Projectile(
    val initialPosition: Vector2D,
    val initialSpeed: Double,  // v0 en m/s
    val angleRadians: Double,  // ángulo en radianes
    val gravity: Double = 9.81 // g en m/s^2
) : PhysicsBody {

    // Componentes iniciales de velocidad
    private val v0x: Double = initialSpeed * cos(angleRadians)
    private val v0y: Double = initialSpeed * sin(angleRadians)

    /**
     * Altura máxima que alcanzará el proyectil
     */
    val maxHeight: Double
        get() = initialPosition.y + (v0y * v0y) / (2 * gravity)

    /**
     * Alcance horizontal (si el suelo está en y=0)
     */
    val horizontalRange: Double
        get() {
            val t = getFlightTime()
            return initialPosition.x + v0x * t
        }

    /**
     * Calcula la posición en el tiempo t
     * x(t) = x0 + v0x * t
     * y(t) = y0 + v0y * t - 0.5 * g * t^2
     */
    override fun getPosition(t: Double): Vector2D {
        val x = initialPosition.x + v0x * t
        val y = initialPosition.y + v0y * t - 0.5 * gravity * t * t
        return Vector2D(x, y)
    }

    /**
     * Calcula la velocidad en el tiempo t
     * vx(t) = v0x (constante)
     * vy(t) = v0y - g * t
     */
    override fun getVelocity(t: Double): Vector2D {
        val vx = v0x
        val vy = v0y - gravity * t
        return Vector2D(vx, vy)
    }

    /**
     * Tiempo de vuelo: cuando y(t) = 0
     * y0 + v0y * t - 0.5 * g * t^2 = 0
     * 0.5 * g * t^2 - v0y * t - y0 = 0
     * t = (v0y ± sqrt(v0y^2 + 2 * g * y0)) / g
     * tomamos la solución positiva
     */
    override fun getFlightTime(): Double {
        if (initialPosition.y == 0.0 && v0y <= 0) return 0.0

        val discriminant = v0y * v0y + 2 * gravity * initialPosition.y
        if (discriminant < 0) return 0.0

        val t1 = (v0y + sqrt(discriminant)) / gravity
        val t2 = (v0y - sqrt(discriminant)) / gravity

        return if (t1 > 0 && t2 > 0) maxOf(t1, t2) else maxOf(t1, t2, 0.0)
    }

    override fun toString(): String {
        return "Projectile(pos=$initialPosition, v0=$initialSpeed m/s, angle=${Math.toDegrees(angleRadians)}°)"
    }
}

