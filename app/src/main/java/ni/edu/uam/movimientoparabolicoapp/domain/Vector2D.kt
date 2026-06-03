package ni.edu.uam.movimientoparabolicoapp.domain

import kotlin.math.hypot

/**
 * Data class que representa un vector en el plano cartesiano de dos dimensiones.
 * 
 * Es la unidad básica de posición y velocidad en todo el motor de física.
 */
data class Vector2D(
    val x: Double = 0.0,
    val y: Double = 0.0
) {
    /**
     * Calcula el módulo o magnitud del vector.
     * Representa la rapidez si el vector es de velocidad, o el radio vector si es posición.
     */
    val magnitude: Double
        get() = hypot(x, y)

    /**
     * Sobrecarga de operadores aritméticos para permitir operaciones matemáticas
     * directas entre objetos Vector2D (ej: vector1 + vector2).
     */
    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
    operator fun minus(other: Vector2D) = Vector2D(x - other.x, y - other.y)
    operator fun times(scalar: Double) = Vector2D(x * scalar, y * scalar)
    operator fun div(scalar: Double) = Vector2D(x / scalar, y / scalar)

    /**
     * Calcula la distancia en línea recta entre dos puntos (Euclidiana).
     * Es la operación fundamental utilizada por el [CollisionDetector].
     */
    fun distanceTo(other: Vector2D): Double = (this - other).magnitude

    override fun toString(): String = "(${String.format("%.2f", x)}, ${String.format("%.2f", y)})"
}
