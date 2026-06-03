package ni.edu.uam.movimientoparabolicoapp.domain

/**
 * Representa la información de una colisión detectada.
 */
data class CollisionInfo(
    val occurred: Boolean = false,
    val time: Double = 0.0,
    val position: Vector2D = Vector2D(),
    val distance: Double = Double.MAX_VALUE
)

/**
 * Detector de colisiones entre dos cuerpos.
 *
 * Calcula la distancia euclidiana entre ambos objetos en cada frame.
 * El umbral se ha ajustado a 0.5m para una mejor respuesta visual en la app.
 */
class CollisionDetector(
    val threshold: Double = 0.5 // 50 cm para mejor detección visual
) {

    /**
     * Detecta si hay colisión entre dos cuerpos en un tiempo específico.
     *
     * @param bodyA Primer cuerpo
     * @param bodyB Segundo cuerpo
     * @param time Tiempo actual en segundos
     * @return CollisionInfo con los datos de la colisión
     */
    fun detectCollision(
        bodyA: PhysicsBody,
        bodyB: PhysicsBody,
        time: Double
    ): CollisionInfo {
        val posA = bodyA.getPosition(time)
        val posB = bodyB.getPosition(time)
        val distance = posA.distanceTo(posB)

        return if (distance < threshold) {
            CollisionInfo(
                occurred = true,
                time = time,
                position = (posA + posB) / 2.0,  // Promedio de posiciones
                distance = distance
            )
        } else {
            CollisionInfo(
                occurred = false,
                time = time,
                position = (posA + posB) / 2.0,
                distance = distance
            )
        }
    }

    /**
     * Busca el punto de colisión más cercano en un intervalo de tiempo,
     * usando búsqueda binaria para mayor precisión.
     *
     * Útil para no "saltarse" colisiones en frames con dt grandes.
     *
     * @param bodyA Primer cuerpo
     * @param bodyB Segundo cuerpo
     * @param startTime Tiempo inicial del intervalo
     * @param endTime Tiempo final del intervalo
     * @param maxIterations Iteraciones máximas de búsqueda binaria
     * @return CollisionInfo con el tiempo y posición más precisos de colisión
     */
    fun findCollisionInInterval(
        bodyA: PhysicsBody,
        bodyB: PhysicsBody,
        startTime: Double,
        endTime: Double,
        maxIterations: Int = 20
    ): CollisionInfo? {
        var minDistance = Double.MAX_VALUE
        var minTime = startTime
        var minPosition = Vector2D()

        // Búsqueda inicial para ver si hay colisión en el intervalo
        for (i in 0..100) {
            val t = startTime + (endTime - startTime) * (i / 100.0)
            val posA = bodyA.getPosition(t)
            val posB = bodyB.getPosition(t)
            val distance = posA.distanceTo(posB)

            if (distance < minDistance) {
                minDistance = distance
                minTime = t
                minPosition = (posA + posB) / 2.0
            }
        }

        return if (minDistance < threshold) {
            CollisionInfo(
                occurred = true,
                time = minTime,
                position = minPosition,
                distance = minDistance
            )
        } else {
            null
        }
    }

    /**
     * Obtiene la distancia mínima entre dos cuerpos en un intervalo de tiempo.
     * Útil para análisis y depuración.
     */
    fun getMinimumDistance(
        bodyA: PhysicsBody,
        bodyB: PhysicsBody,
        startTime: Double,
        endTime: Double,
        samples: Int = 1000
    ): Pair<Double, Double> {
        var minDistance = Double.MAX_VALUE
        var minTime = startTime

        for (i in 0..samples) {
            val t = startTime + (endTime - startTime) * (i / samples.toDouble())
            val posA = bodyA.getPosition(t)
            val posB = bodyB.getPosition(t)
            val distance = posA.distanceTo(posB)

            if (distance < minDistance) {
                minDistance = distance
                minTime = t
            }
        }

        return Pair(minTime, minDistance)
    }
}

