package ni.edu.uam.movimientoparabolicoapp.domain

/**
 * Interfaz que representa un cuerpo en movimiento parabólico.
 * Cada cuerpo tiene una posición y velocidad que pueden calcularse en un instante t.
 */
interface PhysicsBody {
    /**
     * Obtiene la posición del cuerpo en el tiempo t (en segundos)
     */
    fun getPosition(t: Double): Vector2D

    /**
     * Obtiene la velocidad del cuerpo en el tiempo t (en segundos)
     */
    fun getVelocity(t: Double): Vector2D

    /**
     * Obtiene la rapidez (magnitud del vector velocidad) en el tiempo t
     */
    fun getSpeed(t: Double): Double = getVelocity(t).magnitude

    /**
     * Tiempo máximo que el cuerpo permanece en el aire (sobre y=0)
     */
    fun getFlightTime(): Double
}

