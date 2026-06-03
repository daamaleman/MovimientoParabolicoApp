package ni.edu.uam.movimientoparabolicoapp.domain

/**
 * Clase que encapsula los datos resultantes de una colisión.
 * 
 * @property occurred Indica si el choque efectivamente sucedió.
 * @property time Instante exacto en segundos donde ocurrió el contacto.
 * @property position Coordenada (x,y) del punto de impacto.
 * @property distance Distancia mínima alcanzada entre los cuerpos.
 */
data class CollisionInfo(
    val occurred: Boolean = false,
    val time: Double = 0.0,
    val position: Vector2D = Vector2D(),
    val distance: Double = Double.MAX_VALUE
)

/**
 * Motor de detección de proximidad entre dos objetos físicos.
 *
 * Utiliza algoritmos de geometría analítica para determinar si dos proyectiles
 * se han encontrado en el espacio-tiempo.
 *
 * @property threshold Distancia mínima (en metros) para considerar que existe un choque.
 *                     Por defecto es 1mm (0.001m), lo que exige una precisión extrema.
 */
class CollisionDetector(
    val threshold: Double = 0.001 
) {

    /**
     * Evalúa la colisión en un único punto temporal.
     * 
     * Compara las posiciones de A y B en el tiempo 't' y calcula su distancia euclidiana.
     */
    fun detectCollision(
        bodyA: PhysicsBody,
        bodyB: PhysicsBody,
        time: Double
    ): CollisionInfo {
        val posA = bodyA.getPosition(time)
        val posB = bodyB.getPosition(time)
        val distance = posA.distanceTo(posB)

        // Si la distancia es menor al umbral (1mm), hay colisión
        return if (distance < threshold) {
            CollisionInfo(
                occurred = true,
                time = time,
                position = (posA + posB) / 2.0, // Punto medio del impacto
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
     * Algoritmo de búsqueda de colisiones por intervalos.
     * 
     * Este método es vital para simulaciones digitales. Debido a que el tiempo avanza
     * en "saltos" (frames), dos objetos rápidos podrían saltarse el uno al otro.
     * Este método escanea el intervalo entre frames para asegurar que no se pierda el impacto.
     *
     * @param startTime Tiempo al inicio del frame.
     * @param endTime Tiempo al final del frame.
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

        // Escaneo de alta resolución (100 muestras) dentro del pequeño intervalo temporal
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
     * Calcula la aproximación máxima (distancia mínima) entre dos cuerpos en un rango dado.
     * 
     * @return Un par que contiene (instante de máxima cercanía, distancia mínima).
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
