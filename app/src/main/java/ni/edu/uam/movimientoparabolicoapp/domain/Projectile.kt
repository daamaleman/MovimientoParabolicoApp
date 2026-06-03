package ni.edu.uam.movimientoparabolicoapp.domain

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Representa el Objeto A (Proyectil) de la simulación.
 *
 * Implementa las ecuaciones clásicas de la cinemática para un objeto lanzado 
 * con una rapidez inicial y un ángulo determinado.
 *
 * Fórmulas Físicas:
 * 1. Descomposición de velocidad: v0x = v0·cos(θ), v0y = v0·sin(θ)
 * 2. Posición X (MRU): x = x0 + v0x·t
 * 3. Posición Y (MRUV): y = y0 + v0y·t - 0.5·g·t²
 */
class Projectile(
    val initialPosition: Vector2D,
    val initialSpeed: Double,  
    val angleRadians: Double,  
    val gravity: Double = 9.81 
) : PhysicsBody {

    // Componentes de velocidad inicial calculados una sola vez al instanciar
    private val v0x: Double = initialSpeed * cos(angleRadians)
    private val v0y: Double = initialSpeed * sin(angleRadians)

    /**
     * Calcula la posición (x, y) en cualquier segundo 't'.
     * 
     * Implementa la superposición de movimientos: Rectilíneo Uniforme en el eje X
     * y Uniformemente Variado (caída libre) en el eje Y.
     */
    override fun getPosition(t: Double): Vector2D {
        val x = initialPosition.x + v0x * t
        val y = initialPosition.y + v0y * t - 0.5 * gravity * t * t
        return Vector2D(x, y)
    }

    /**
     * Calcula el vector velocidad (vx, vy) en el tiempo 't'.
     * 
     * Nota: vx es constante ya que no hay fricción con el aire.
     * vy decrece linealmente debido a la gravedad.
     */
    override fun getVelocity(t: Double): Vector2D {
        val vx = v0x
        val vy = v0y - gravity * t
        return Vector2D(vx, vy)
    }

    /**
     * Resuelve la ecuación cuadrática de posición vertical para hallar el tiempo
     * en el que el proyectil impacta contra el suelo (y = 0).
     * 
     * Se utiliza la fórmula general para ecuaciones de segundo grado.
     */
    override fun getFlightTime(): Double {
        // Caso especial: si ya está en el suelo y no tiene impulso hacia arriba
        if (initialPosition.y <= 0.0 && v0y <= 0) return 0.0

        // Discriminante: b² - 4ac
        val discriminant = v0y * v0y + 2 * gravity * initialPosition.y
        if (discriminant < 0) return 0.0

        // Obtenemos las dos posibles soluciones
        val t1 = (v0y + sqrt(discriminant)) / gravity
        val t2 = (v0y - sqrt(discriminant)) / gravity

        // Retornamos la solución positiva (el tiempo futuro)
        return if (t1 > 0 && t2 > 0) maxOf(t1, t2) else maxOf(t1, t2, 0.0)
    }
}
