package ni.edu.uam.movimientoparabolicoapp.data

import kotlin.math.PI

/**
 * Parámetros que definen el estado de la simulación.
 *
 * Esta data class encapsula TODOS los controles editables de la UI:
 * - Proyectil A: velocidad, ángulo, posición
 * - Objetivo B: posición, velocidad (opcional)
 * - Entorno: gravedad, velocidad de animación
 */
data class SimulationParams(
    // Proyectil A - velocidad inicial (m/s)
    val projectileInitialSpeed: Double = 15.0,

    // Proyectil A - ángulo de lanzamiento (radianes, pero podemos convertir desde grados)
    val projectileAngleDegrees: Double = 38.7,

    // Proyectil A - posición inicial (metros)
    val projectileX: Double = 0.0,
    val projectileY: Double = 0.0,

    // Objetivo B - posición inicial (metros)
    val targetX: Double = 10.0,
    val targetY: Double = 8.0,

    // Objetivo B - velocidad inicial (m/s). Si es 0, cae libre.
    val targetInitialSpeed: Double = 0.0,

    // Entorno - aceleración gravitacional (m/s^2)
    val gravity: Double = 9.81,

    // Animación - multiplicador de velocidad
    val animationSpeedMultiplier: Double = 1.0
) {
    /**
     * Convierte el ángulo de grados a radianes para usar en cálculos de física.
     */
    fun getProjectileAngleRadians(): Double = projectileAngleDegrees * PI / 180.0

    /**
     * Crea una copia con parámetros preestablecidos para el escenario "tiro al mono".
     */
    companion object {
        fun monkeyHunterPreset() = SimulationParams(
            projectileInitialSpeed = 15.0,
            projectileAngleDegrees = 38.66,  // atan2(8, 10) en grados ≈ 38.7°
            projectileX = 0.0,
            projectileY = 0.0,
            targetX = 10.0,
            targetY = 8.0,
            targetInitialSpeed = 0.0,  // El objetivo se suelta libremente
            gravity = 9.81,
            animationSpeedMultiplier = 1.0
        )

        /**
         * Preset para Luna (gravedad menor)
         */
        fun moonPreset() = SimulationParams(
            projectileInitialSpeed = 15.0,
            projectileAngleDegrees = 38.66,
            projectileX = 0.0,
            projectileY = 0.0,
            targetX = 10.0,
            targetY = 8.0,
            targetInitialSpeed = 0.0,
            gravity = 1.62,
            animationSpeedMultiplier = 1.0
        )

        /**
         * Preset para Marte
         */
        fun marsPreset() = SimulationParams(
            projectileInitialSpeed = 15.0,
            projectileAngleDegrees = 38.66,
            projectileX = 0.0,
            projectileY = 0.0,
            targetX = 10.0,
            targetY = 8.0,
            targetInitialSpeed = 0.0,
            gravity = 3.71,
            animationSpeedMultiplier = 1.0
        )
    }
}

