package ni.edu.uam.movimientoparabolicoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import ni.edu.uam.movimientoparabolicoapp.domain.CollisionInfo
import ni.edu.uam.movimientoparabolicoapp.domain.Vector2D

/**
 * Canvas que dibuja la simulación en 2D.
 *
 * Muestra:
 * - Suelo y cuadrícula de referencia
 * - Trayectorias completas punteadas
 * - Trayectorias recorridas sólidas
 * - Dos objetos de colores diferentes
 * - Punto de colisión si ocurrió
 */
@Composable
fun SimulationCanvas(
    modifier: Modifier = Modifier,
    currentTime: Double,
    projectilePos: Vector2D,
    targetPos: Vector2D,
    projectileTrajectory: List<Pair<Double, Vector2D>>,
    targetTrajectory: List<Pair<Double, Vector2D>>,
    collisionInfo: CollisionInfo?,
    maxSimulationTime: Double
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color(0xFFf0f7fd))
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Calcula los límites del mundo para encuadre automático
            val worldBounds = calculateWorldBounds(
                projectileTrajectory,
                targetTrajectory,
                collisionInfo
            )

            // ========== Dibuja fondo y suelo ==========
            drawRect(
                color = Color(0xFFfbf8ff),
                size = size
            )

            // Suelo (línea verde en la parte inferior)
            val groundY = canvasHeight - 30f
            drawLine(
                color = Color(0xFF3c7846),
                start = Offset(0f, groundY),
                end = Offset(canvasWidth, groundY),
                strokeWidth = 2f
            )

            // Relleno del suelo
            drawRect(
                color = Color(0xFF3c7846),
                topLeft = Offset(0f, groundY),
                size = androidx.compose.ui.geometry.Size(canvasWidth, canvasHeight - groundY),
                alpha = 0.2f
            )

            // ========== Dibuja cuadrícula de referencia ==========
            drawGrid(worldBounds, canvasWidth, canvasHeight)

            // ========== Dibuja trayectorias punteadas (predicción) ==========
            if (projectileTrajectory.isNotEmpty()) {
                drawTrajectory(
                    trajectory = projectileTrajectory,
                    worldBounds = worldBounds,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    color = Color(0xFF4f5bd5),
                    strokeWidth = 1.5f,
                    isDashed = true
                )
            }

            if (targetTrajectory.isNotEmpty()) {
                drawTrajectory(
                    trajectory = targetTrajectory,
                    worldBounds = worldBounds,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    color = Color(0xFFe0552b),
                    strokeWidth = 1.5f,
                    isDashed = true
                )
            }

            // ========== Dibuja trayectorias recorridas (sólidas) ==========
            if (projectileTrajectory.isNotEmpty()) {
                val traveledProj = projectileTrajectory.filter { (t, _) -> t <= currentTime }
                drawTrajectory(
                    trajectory = traveledProj,
                    worldBounds = worldBounds,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    color = Color(0xFF4f5bd5),
                    strokeWidth = 2.5f,
                    isDashed = false
                )
            }

            if (targetTrajectory.isNotEmpty()) {
                val traveledTarget = targetTrajectory.filter { (t, _) -> t <= currentTime }
                drawTrajectory(
                    trajectory = traveledTarget,
                    worldBounds = worldBounds,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    color = Color(0xFFe0552b),
                    strokeWidth = 2.5f,
                    isDashed = false
                )
            }

            // ========== Dibuja objetos ==========
            drawObject(
                pos = projectilePos,
                worldBounds = worldBounds,
                canvasWidth = canvasWidth,
                canvasHeight = canvasHeight,
                color = Color(0xFF4f5bd5),
                radius = 8f
            )

            drawObject(
                pos = targetPos,
                worldBounds = worldBounds,
                canvasWidth = canvasWidth,
                canvasHeight = canvasHeight,
                color = Color(0xFFe0552b),
                radius = 8f
            )

            // ========== Dibuja punto de colisión ==========
            if (collisionInfo != null && collisionInfo.occurred) {
                drawCollisionPoint(
                    pos = collisionInfo.position,
                    worldBounds = worldBounds,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight
                )
            }

            // ========== Dibuja información en la esquina ==========
            // (Se deja en los readouts UI, no en el canvas)
        }
    }
}

private fun DrawScope.calculateWorldBounds(
    projTraj: List<Pair<Double, Vector2D>>,
    targetTraj: List<Pair<Double, Vector2D>>,
    collision: CollisionInfo?
): WorldBounds {
    val allPositions = (projTraj.map { it.second } + targetTraj.map { it.second }).toMutableList()
    if (collision != null) allPositions.add(collision.position)

    if (allPositions.isEmpty()) {
        return WorldBounds(maxX = 10.0, maxY = 10.0)
    }

    val maxX = (allPositions.maxOfOrNull { it.x } ?: 10.0) + 2.0
    val maxY = (allPositions.maxOfOrNull { it.y } ?: 10.0) + 2.0

    return WorldBounds(maxX = maxX, maxY = maxY)
}

private data class WorldBounds(val maxX: Double, val maxY: Double)

private fun DrawScope.world2Canvas(
    worldPos: Vector2D,
    worldBounds: WorldBounds,
    canvasWidth: Float,
    canvasHeight: Float
): Offset {
    val canvasX = 30f + ((worldPos.x / worldBounds.maxX) * (canvasWidth - 45f)).toFloat()
    val canvasY = canvasHeight - 30f - ((worldPos.y / worldBounds.maxY) * (canvasHeight - 50f)).toFloat()
    return Offset(canvasX, canvasY)
}

private fun DrawScope.drawGrid(
    worldBounds: WorldBounds,
    canvasWidth: Float,
    canvasHeight: Float
) {
    val gridSpacing = kotlin.math.ceil(worldBounds.maxX / 6).toInt().coerceAtLeast(1)

    for (gx in 0..worldBounds.maxX.toInt() step gridSpacing) {
        val offset = world2Canvas(
            Vector2D(gx.toDouble(), 0.0),
            worldBounds,
            canvasWidth,
            canvasHeight
        )
        drawLine(
            color = Color(0x20464f),
            start = Offset(offset.x, 12f),
            end = Offset(offset.x, canvasHeight - 30f),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawTrajectory(
    trajectory: List<Pair<Double, Vector2D>>,
    worldBounds: WorldBounds,
    canvasWidth: Float,
    canvasHeight: Float,
    color: Color,
    strokeWidth: Float,
    isDashed: Boolean
) {
    if (trajectory.isEmpty()) return

    val points = trajectory
        .filter { (_, pos) -> pos.y >= -0.5 }
        .map { (_, pos) -> world2Canvas(pos, worldBounds, canvasWidth, canvasHeight) }

    if (points.size < 2) return

    val path = androidx.compose.ui.graphics.Path()
    path.moveTo(points[0].x, points[0].y)
    for (i in 1 until points.size) {
        path.lineTo(points[i].x, points[i].y)
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth, pathEffect = if (isDashed) {
            androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                intervals = floatArrayOf(4f, 4f)
            )
        } else null)
    )
}

private fun DrawScope.drawObject(
    pos: Vector2D,
    worldBounds: WorldBounds,
    canvasWidth: Float,
    canvasHeight: Float,
    color: Color,
    radius: Float
) {
    val canvasPos = world2Canvas(pos, worldBounds, canvasWidth, canvasHeight)

    // Círculo del objeto
    drawCircle(
        color = color,
        radius = radius,
        center = canvasPos
    )

    // Destello blanco
    drawCircle(
        color = Color.White,
        radius = 2.5f,
        center = Offset(canvasPos.x - 2.5f, canvasPos.y - 2.5f),
        alpha = 0.5f
    )
}

private fun DrawScope.drawCollisionPoint(
    pos: Vector2D,
    worldBounds: WorldBounds,
    canvasWidth: Float,
    canvasHeight: Float
) {
    val canvasPos = world2Canvas(pos, worldBounds, canvasWidth, canvasHeight)

    // Círculo exterior verde
    drawCircle(
        color = Color(0xFF1f8a4c),
        radius = 11f,
        center = canvasPos,
        alpha = 0.9f
    )

    // Símbolo de choque
    drawCircle(
        color = Color.White,
        radius = 3f,
        center = canvasPos
    )
}



