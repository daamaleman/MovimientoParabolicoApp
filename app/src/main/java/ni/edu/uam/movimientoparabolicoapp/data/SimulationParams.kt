package ni.edu.uam.movimientoparabolicoapp.data

import kotlin.math.PI

/**
 * Modelo de datos que centraliza todas las variables configurables por el usuario.
 * 
 * Contiene los valores de entrada para los objetos A y B, así como las constantes
 * del entorno físico.
 */
data class SimulationParams(
    // Parámetros del Proyectil (Objeto A)
    val projectileInitialSpeed: Double = 15.0,
    val projectileAngleDegrees: Double = 38.7,
    val projectileX: Double = 0.0,
    val projectileY: Double = 0.0,

    // Parámetros del Objetivo (Objeto B)
    val targetX: Double = 10.0,
    val targetY: Double = 8.0,
    val targetInitialSpeed: Double = 0.0,

    // Constantes del Entorno y Sistema
    val gravity: Double = 9.81,
    val animationSpeedMultiplier: Double = 1.0
) {
    /**
     * Convierte el ángulo ingresado en grados a radianes.
     * 
     * Las funciones trigonométricas de Kotlin (cos, sin, tan) requieren el ángulo
     * en radianes para operar correctamente.
     */
    fun getProjectileAngleRadians(): Double = projectileAngleDegrees * PI / 180.0

    /**
     * Colección de configuraciones predefinidas (Presets).
     */
    companion object {
        /**
         * Escenario Clásico: El Proyectil apunta directamente a la posición inicial
         * de un objetivo que se soltará en caída libre.
         */
        fun monkeyHunterPreset() = SimulationParams(
            projectileInitialSpeed = 15.0,
            projectileAngleDegrees = 38.66, 
            projectileX = 0.0,
            projectileY = 0.0,
            targetX = 10.0,
            targetY = 8.0,
            targetInitialSpeed = 0.0,
            gravity = 9.81,
            animationSpeedMultiplier = 1.0
        )

        /** Preset con gravedad de la Luna (1.62 m/s²) */
        fun moonPreset() = SimulationParams(
            projectileInitialSpeed = 15.0,
            projectileAngleDegrees = 38.66,
            gravity = 1.62
        )

        /** Preset con gravedad de Marte (3.71 m/s²) */
        fun marsPreset() = SimulationParams(
            projectileInitialSpeed = 15.0,
            projectileAngleDegrees = 38.66,
            gravity = 3.71
        )
    }
}
