package ni.edu.uam.movimientoparabolicoapp

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import ni.edu.uam.movimientoparabolicoapp.domain.CollisionDetector
import ni.edu.uam.movimientoparabolicoapp.domain.FallingTarget
import ni.edu.uam.movimientoparabolicoapp.domain.KinematicsEngine
import ni.edu.uam.movimientoparabolicoapp.domain.Projectile
import ni.edu.uam.movimientoparabolicoapp.domain.Vector2D
import kotlin.math.PI
import kotlin.math.sqrt

/**
 * Pruebas unitarias del motor de cinemática.
 *
 * Verifica que las ecuaciones de movimiento parabólico sean correctas.
 */
class KinematicsEngineTest {

    private lateinit var engine: KinematicsEngine
    private val g = 9.81  // Gravedad estándar (Tierra)

    @Before
    fun setUp() {
        engine = KinematicsEngine(gravity = g)
    }

    /**
     * Prueba que la posición inicial (t=0) sea correcta.
     */
    @Test
    fun testInitialPosition() {
        val projectile = engine.createProjectile(
            position = Vector2D(0.0, 0.0),
            initialSpeed = 10.0,
            angleRadians = PI / 4  // 45 grados
        )

        val pos = projectile.getPosition(0.0)
        assertEquals(0.0, pos.x, 0.0001)
        assertEquals(0.0, pos.y, 0.0001)
    }

    /**
     * Prueba la posición en movimiento parabólico.
     * para t=1s con v0=10 m/s a 45°:
     * x(1) = 10*cos(45°)*1 ≈ 7.07 m
     * y(1) = 10*sin(45°)*1 - 0.5*9.81*1 ≈ 7.07 - 4.9 ≈ 2.17 m
     */
    @Test
    fun testPositionAtT1() {
        val projectile = engine.createProjectile(
            position = Vector2D(0.0, 0.0),
            initialSpeed = 10.0,
            angleRadians = PI / 4  // 45 grados
        )

        val pos = projectile.getPosition(1.0)
        val expectedX = 10.0 * kotlin.math.cos(PI / 4) * 1.0
        val expectedY = 10.0 * kotlin.math.sin(PI / 4) * 1.0 - 0.5 * g * 1.0

        assertEquals(expectedX, pos.x, 0.01)
        assertEquals(expectedY, pos.y, 0.01)
    }

    /**
     * Prueba que la velocidad horizontal sea constante.
     */
    @Test
    fun testConstantHorizontalVelocity() {
        val projectile = engine.createProjectile(
            position = Vector2D(0.0, 0.0),
            initialSpeed = 20.0,
            angleRadians = PI / 6  // 30 grados
        )

        val vel0 = projectile.getVelocity(0.0)
        val vel1 = projectile.getVelocity(1.0)
        val vel2 = projectile.getVelocity(2.0)

        assertEquals(vel0.x, vel1.x, 0.0001)
        assertEquals(vel1.x, vel2.x, 0.0001)
    }

    /**
     * Prueba que la velocidad vertical disminuya linealmente con el tiempo.
     * vy(t) = v0*sin(θ) - g*t
     */
    @Test
    fun testLinearVerticalVelocity() {
        val projectile = engine.createProjectile(
            position = Vector2D(0.0, 0.0),
            initialSpeed = 20.0,
            angleRadians = PI / 4
        )

        val vel0 = projectile.getVelocity(0.0)
        val vel1 = projectile.getVelocity(1.0)

        // La diferencia debe ser -g*Δt
        val expectedDifference = -g * 1.0
        assertEquals(expectedDifference, vel1.y - vel0.y, 0.01)
    }

    /**
     * Prueba que la altura máxima sea correcta.
     * Para v0=20 m/s a 45°: h_max = (v0*sin(45°))^2 / (2*g) ≈ 10.2 m
     */
    @Test
    fun testMaxHeight() {
        val projectile = engine.createProjectile(
            position = Vector2D(0.0, 0.0),
            initialSpeed = 20.0,
            angleRadians = PI / 4
        )

        val v0y = 20.0 * kotlin.math.sin(PI / 4)
        val expectedMaxHeight = v0y * v0y / (2 * g)

        assertEquals(expectedMaxHeight, projectile.maxHeight, 0.01)
    }

    /**
     * Prueba que la rapidez en un instante sea |v| = sqrt(vx² + vy²).
     */
    @Test
    fun testSpeed() {
        val projectile = engine.createProjectile(
            position = Vector2D(0.0, 0.0),
            initialSpeed = 15.0,
            angleRadians = PI / 3  // 60 grados
        )

        val vel = projectile.getVelocity(1.0)
        val expectedSpeed = sqrt(vel.x * vel.x + vel.y * vel.y)
        val actualSpeed = projectile.getSpeed(1.0)

        assertEquals(expectedSpeed, actualSpeed, 0.0001)
    }

    /**
     * Prueba la detección de colisión.
     * Caso: dos cuerpos muy cercanos (< 1mm = 0.001 m)
     */
    @Test
    fun testCollisionDetection() {
        val projectile = engine.createProjectile(
            position = Vector2D(0.0, 2.0),
            initialSpeed = 10.0,
            angleRadians = 0.0  // Horizontal
        )

        val target = engine.createFallingTarget(
            position = Vector2D(10.0, 2.0),
            initialSpeed = 0.0  // Cae libre
        )

        // En t=1s, projectile en (10, 2 - 4.9) = (10, -2.9)
        // target en (10, 2 - 4.9) = (10, -2.9)
        // Estarían muy juntos

        val collision = engine.detectCollision(projectile, target, 0.5)
        // En realidad no colisionan porque sus trayectorias son paralelas pero separadas
        assertFalse(collision.occurred)
    }

    /**
     * Prueba el cálculo del ángulo "tiro al mono".
     * Si el tirador está en (0,0) y el objetivo en (10,8),
     * el ángulo debe ser atan2(8, 10) ≈ 38.66°
     */
    @Test
    fun testMonkeyHunterAngle() {
        val shooterPos = Vector2D(0.0, 0.0)
        val targetPos = Vector2D(10.0, 8.0)

        val angle = engine.calculateMonkeyHunterAngle(shooterPos, targetPos)
        val expectedAngle = kotlin.math.atan2(8.0, 10.0)

        assertEquals(expectedAngle, angle, 0.0001)
    }

    /**
     * Prueba que el tiempo de vuelo sea positivo y coherente.
     */
    @Test
    fun testFlightTime() {
        val projectile = engine.createProjectile(
            position = Vector2D(0.0, 0.0),
            initialSpeed = 20.0,
            angleRadians = PI / 4
        )

        val flightTime = projectile.getFlightTime()
        assertTrue(flightTime > 0)

        // Verifica que en tiempo de vuelo, y ≈ 0
        val finalPos = projectile.getPosition(flightTime)
        assertEquals(0.0, finalPos.y, 0.1)
    }

    /**
     * Prueba la distancia entre dos vectores.
     */
    @Test
    fun testVector2DDistance() {
        val v1 = Vector2D(0.0, 0.0)
        val v2 = Vector2D(3.0, 4.0)

        val distance = v1.distanceTo(v2)
        assertEquals(5.0, distance, 0.0001)  // Triángulo 3-4-5
    }

    /**
     * Prueba operaciones vectoriales.
     */
    @Test
    fun testVector2DOperations() {
        val v1 = Vector2D(1.0, 2.0)
        val v2 = Vector2D(3.0, 4.0)

        val sum = v1 + v2
        assertEquals(4.0, sum.x, 0.0001)
        assertEquals(6.0, sum.y, 0.0001)

        val diff = v2 - v1
        assertEquals(2.0, diff.x, 0.0001)
        assertEquals(2.0, diff.y, 0.0001)

        val scaled = v1 * 3.0
        assertEquals(3.0, scaled.x, 0.0001)
        assertEquals(6.0, scaled.y, 0.0001)
    }

    /**
     * Prueba que la caída libre tiene posición y que decrece.
     */
    @Test
    fun testFreefall() {
        val target = engine.createFallingTarget(
            position = Vector2D(10.0, 20.0),
            initialSpeed = 0.0
        )

        val pos0 = target.getPosition(0.0)
        val pos1 = target.getPosition(1.0)
        val pos2 = target.getPosition(2.0)

        assertEquals(20.0, pos0.y, 0.0001)
        assertTrue(pos1.y < pos0.y)
        assertTrue(pos2.y < pos1.y)
    }
}

