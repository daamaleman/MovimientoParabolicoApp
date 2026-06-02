package ni.edu.uam.movimientoparabolicoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.movimientoparabolicoapp.domain.CollisionInfo
import ni.edu.uam.movimientoparabolicoapp.domain.Vector2D
import ni.edu.uam.movimientoparabolicoapp.ui.theme.CanvasBg
import ni.edu.uam.movimientoparabolicoapp.ui.theme.CollisionGreen
import ni.edu.uam.movimientoparabolicoapp.ui.theme.GroundGreen
import ni.edu.uam.movimientoparabolicoapp.ui.theme.ProjectileBlue
import ni.edu.uam.movimientoparabolicoapp.ui.theme.TargetOrange

/**
 * Canvas que dibuja la simulación en 2D.
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
            .height(260.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(color = CanvasBg)
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val worldBounds = calculateWorldBounds(
                projectileTrajectory,
                targetTrajectory,
                collisionInfo
            )

            // Fondo blanco sutil
            drawRect(color = Color.White.copy(alpha = 0.5f), size = size)

            // Suelo (según mockup)
            val groundY = canvasHeight - 40f
            drawRect(
                color = GroundGreen,
                topLeft = Offset(0f, groundY),
                size = androidx.compose.ui.geometry.Size(canvasWidth, canvasHeight - groundY)
            )
            drawLine(
                color = Color(0xFF88A085),
                start = Offset(0f, groundY),
                end = Offset(canvasWidth, groundY),
                strokeWidth = 2f
            )

            // Cuadrícula sutil
            drawGrid(worldBounds, canvasWidth, canvasHeight)

            // Trayectorias predicción (punteadas)
            if (projectileTrajectory.isNotEmpty()) {
                drawTrajectory(
                    trajectory = projectileTrajectory,
                    worldBounds = worldBounds,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    color = ProjectileBlue,
                    strokeWidth = 1.2f,
                    isDashed = true
                )
            }

            if (targetTrajectory.isNotEmpty()) {
                drawTrajectory(
                    trajectory = targetTrajectory,
                    worldBounds = worldBounds,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    color = TargetOrange,
                    strokeWidth = 1.2f,
                    isDashed = true
                )
            }

            // Trayectorias recorridas (sólidas)
            if (projectileTrajectory.isNotEmpty()) {
                val traveledProj = projectileTrajectory.filter { (t, _) -> t <= currentTime }
                drawTrajectory(
                    trajectory = traveledProj,
                    worldBounds = worldBounds,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    color = ProjectileBlue,
                    strokeWidth = 2f,
                    isDashed = false
                )
            }

            // Objetos
            drawObject(
                pos = projectilePos,
                worldBounds = worldBounds,
                canvasWidth = canvasWidth,
                canvasHeight = canvasHeight,
                color = ProjectileBlue,
                radius = 10f
            )

            drawObject(
                pos = targetPos,
                worldBounds = worldBounds,
                canvasWidth = canvasWidth,
                canvasHeight = canvasHeight,
                color = TargetOrange,
                radius = 10f
            )

            // Colisión
            if (collisionInfo != null && collisionInfo.occurred) {
                drawCollisionPoint(
                    pos = collisionInfo.position,
                    worldBounds = worldBounds,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight
                )
            }
        }

        // Overlay de tiempo (t = 0.00 s)
        Box(
            modifier = Modifier
                .padding(12.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "t = ${String.format("%.2f", currentTime)} s",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
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

    if (allPositions.isEmpty()) return WorldBounds(maxX = 12.0, maxY = 10.0)

    val maxX = (allPositions.maxOfOrNull { it.x } ?: 10.0) + 3.0
    val maxY = (allPositions.maxOfOrNull { it.y } ?: 8.0) + 3.0

    return WorldBounds(maxX = maxX, maxY = maxY)
}

private data class WorldBounds(val maxX: Double, val maxY: Double)

private fun DrawScope.world2Canvas(
    worldPos: Vector2D,
    worldBounds: WorldBounds,
    canvasWidth: Float,
    canvasHeight: Float
): Offset {
    val canvasX = 40f + ((worldPos.x / worldBounds.maxX) * (canvasWidth - 80f)).toFloat()
    val canvasY = canvasHeight - 40f - ((worldPos.y / worldBounds.maxY) * (canvasHeight - 80f)).toFloat()
    return Offset(canvasX, canvasY)
}

private fun DrawScope.drawGrid(
    worldBounds: WorldBounds,
    canvasWidth: Float,
    canvasHeight: Float
) {
    val gridCount = 8
    val color = Color.LightGray.copy(alpha = 0.3f)
    
    for (i in 0..gridCount) {
        val x = 40f + (i * (canvasWidth - 80f) / gridCount)
        drawLine(color = color, start = Offset(x, 0f), end = Offset(x, canvasHeight - 40f), strokeWidth = 1f)
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
    val points = trajectory.map { world2Canvas(it.second, worldBounds, canvasWidth, canvasHeight) }
    if (points.size < 2) return

    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(points[0].x, points[0].y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidth,
            pathEffect = if (isDashed) androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(8f, 8f)) else null
        ),
        alpha = if (isDashed) 0.4f else 1.0f
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
    drawCircle(color = color, radius = radius, center = canvasPos)
    drawCircle(color = Color.White, radius = radius * 0.4f, center = Offset(canvasPos.x - radius*0.3f, canvasPos.y - radius*0.3f), alpha = 0.4f)
}

private fun DrawScope.drawCollisionPoint(
    pos: Vector2D,
    worldBounds: WorldBounds,
    canvasWidth: Float,
    canvasHeight: Float
) {
    val canvasPos = world2Canvas(pos, worldBounds, canvasWidth, canvasHeight)
    drawCircle(color = CollisionGreen, radius = 14f, center = canvasPos, alpha = 0.3f)
    drawCircle(color = CollisionGreen, radius = 6f, center = canvasPos)
}
