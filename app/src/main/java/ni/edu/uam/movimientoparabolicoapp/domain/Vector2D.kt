package ni.edu.uam.movimientoparabolicoapp.domain

import kotlin.math.hypot
import kotlin.math.sqrt

/**
 * Representa un vector 2D con componentes x, y.
 * Incluye operadores útiles para cálculos de cinemática.
 */
data class Vector2D(
    val x: Double = 0.0,
    val y: Double = 0.0
) {
    /**
     * Magnitud del vector: sqrt(x^2 + y^2)
     */
    val magnitude: Double
        get() = hypot(x, y)

    /**
     * Componentes del vector
     */
    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
    operator fun minus(other: Vector2D) = Vector2D(x - other.x, y - other.y)
    operator fun times(scalar: Double) = Vector2D(x * scalar, y * scalar)
    operator fun div(scalar: Double) = Vector2D(x / scalar, y / scalar)

    /**
     * Distancia euclidiana entre este vector y otro (usada para colisión)
     */
    fun distanceTo(other: Vector2D): Double = (this - other).magnitude

    override fun toString(): String = "(x=$x, y=$y)"
}

